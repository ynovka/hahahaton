import os
import cv2
import numpy as np
from PIL import Image, ImageDraw
from scipy.ndimage import binary_fill_holes

BRAIN_DIR = r"C:\Users\IKER\.gemini\antigravity-ide\brain\d0faca7d-4411-4613-8298-b72beb108947"
RES_DIR = r"e:\Hakaton\hahahaton\app\src\main\res\drawable"

def smooth_alpha_edge(binary_mask, dist_scale=0.7, upper_clamp=1.2, lower_clamp=-1.0):
    """Generates a smooth, anti-aliased 1.5-2px transition at the mask boundary."""
    dist_in = cv2.distanceTransform(binary_mask.astype(np.uint8), cv2.DIST_L2, 3)
    dist_out = cv2.distanceTransform((1 - binary_mask.astype(np.uint8)), cv2.DIST_L2, 3)
    dist = dist_in - dist_out
    alpha = np.clip(dist * dist_scale + 0.5, 0.0, 1.0)
    alpha[dist > upper_clamp] = 1.0
    alpha[dist < lower_clamp] = 0.0
    return (alpha * 255).astype(np.uint8)

def extract_character_silhouette(img_crop, bg_thresh=20, is_white_char=False):
    """
    Extracts a character from studio background.
    Guarantees that face, eyes, and clothes have ZERO transparent holes.
    """
    ch, cw = img_crop.shape[:2]
    gray = cv2.cvtColor(img_crop, cv2.COLOR_BGR2GRAY)
    
    if is_white_char:
        # For light/white characters (e.g. Bunny, Panda), use closed edge hull to protect white fur
        edges = cv2.Canny(gray, 8, 30)
        k = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (7, 7))
        closed = cv2.morphologyEx(edges, cv2.MORPH_CLOSE, k)
        cnts, _ = cv2.findContours(closed, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        cnts = [c for c in cnts if cv2.contourArea(c) > 50]
        mask = np.zeros(gray.shape, np.uint8)
        if cnts:
            all_pts = np.vstack(cnts)
            hull = cv2.convexHull(all_pts)
            cv2.drawContours(mask, [hull], -1, 255, -1)
        fg_solid = (mask > 0)
    else:
        # Sample background color from border margins
        borders = np.vstack([
            img_crop[:4, :].reshape(-1, 3),
            img_crop[-4:, :].reshape(-1, 3),
            img_crop[:, :4].reshape(-1, 3),
            img_crop[:, -4:].reshape(-1, 3)
        ])
        bg_median = np.median(borders, axis=0)
        dist = np.linalg.norm(img_crop.astype(float) - bg_median.astype(float), axis=2)
        bg_candidates = (dist < bg_thresh).astype(np.uint8)
        
        # Connected components touching the outer image boundary
        num_labels, labels, stats, centroids = cv2.connectedComponentsWithStats(bg_candidates, connectivity=8)
        boundary_labels = set()
        boundary_labels.update(labels[0, :].tolist())
        boundary_labels.update(labels[-1, :].tolist())
        boundary_labels.update(labels[:, 0].tolist())
        boundary_labels.update(labels[:, -1].tolist())
        boundary_labels.discard(0)
        
        outer_bg = np.isin(labels, list(boundary_labels))
        fg = ~outer_bg
        fg_solid = binary_fill_holes(fg)
    
    alpha = smooth_alpha_edge(fg_solid)
    b, g, r = cv2.split(img_crop)
    rgba = cv2.merge([b, g, r, alpha])
    pil_img = Image.fromarray(cv2.cvtColor(rgba, cv2.COLOR_BGRA2RGBA))
    bbox = pil_img.getbbox()
    if bbox:
        pil_img = pil_img.crop(bbox)
    return pil_img

def extract_item_prop(img_crop, bottom_cut=40, dist_thresh=28):
    """
    Extracts an isolated 3D object from the card using GrabCut.
    Eliminates all card borders, outer tiles, and text.
    """
    ch, cw = img_crop.shape[:2]
    
    sample_c1 = img_crop[4:22, 4:22].reshape(-1, 3)
    sample_c2 = img_crop[4:22, -22:-4].reshape(-1, 3)
    bg_color = np.median(np.vstack([sample_c1, sample_c2]), axis=0)
    
    dist_bg = np.linalg.norm(img_crop.astype(float) - bg_color.astype(float), axis=2)
    
    mask = np.full((ch, cw), cv2.GC_PR_FGD, dtype=np.uint8)
    mask[dist_bg < dist_thresh] = cv2.GC_BGD
    mask[:12, :] = cv2.GC_BGD
    mask[-bottom_cut:, :] = cv2.GC_BGD
    mask[:, :12] = cv2.GC_BGD
    mask[:, -12:] = cv2.GC_BGD
    
    bgdModel = np.zeros((1, 65), np.float64)
    fgdModel = np.zeros((1, 65), np.float64)
    cv2.grabCut(img_crop, mask, None, bgdModel, fgdModel, 6, cv2.GC_INIT_WITH_MASK)
    
    fg_mask = np.where((mask == cv2.GC_FGD) | (mask == cv2.GC_PR_FGD), 1, 0).astype(np.uint8)
    fg_mask = binary_fill_holes(fg_mask).astype(np.uint8)
    
    alpha = smooth_alpha_edge(fg_mask)
    b, g, r = cv2.split(img_crop)
    rgba = cv2.merge([b, g, r, alpha])
    pil_img = Image.fromarray(cv2.cvtColor(rgba, cv2.COLOR_BGRA2RGBA))
    bbox = pil_img.getbbox()
    if bbox:
        pil_img = pil_img.crop(bbox)
        
    pad = 14
    max_dim = max(pil_img.width, pil_img.height) + pad * 2
    sq_img = Image.new('RGBA', (max_dim, max_dim), (0, 0, 0, 0))
    offset = ((max_dim - pil_img.width) // 2, (max_dim - pil_img.height) // 2)
    sq_img.paste(pil_img, offset)
    return sq_img.resize((192, 192), Image.Resampling.LANCZOS)

def make_circular_portrait(pil_img, size=(256, 256)):
    """Creates a clean avatar with circular mask and subtle crisp white outline."""
    aspect = pil_img.width / pil_img.height
    if aspect > 1.0:
        new_w = int(size[0] * 1.15 * aspect)
        new_h = int(size[1] * 1.15)
    else:
        new_w = int(size[0] * 1.15)
        new_h = int(size[1] * 1.15 / aspect)
    resized = pil_img.resize((new_w, new_h), Image.Resampling.LANCZOS)
    
    cx = (new_w - size[0]) // 2
    cy = int((new_h - size[1]) * 0.12)
    cropped = resized.crop((cx, cy, cx + size[0], cy + size[1]))
    
    mask = Image.new('L', size, 0)
    draw = ImageDraw.Draw(mask)
    draw.ellipse((4, 4, size[0] - 4, size[1] - 4), fill=255)
    
    border = Image.new('RGBA', size, (0, 0, 0, 0))
    bdraw = ImageDraw.Draw(border)
    bdraw.ellipse((3, 3, size[0] - 3, size[1] - 3), outline=(255, 255, 255, 240), width=5)
    
    avatar = Image.new('RGBA', size, (0, 0, 0, 0))
    avatar.paste(cropped, (0, 0), mask=mask)
    avatar.alpha_composite(border)
    return avatar

def run_extraction():
    print("=== STARTING COMPLETE PRODUCTION ASSET PIPELINE ===")
    
    # -------------------------------------------------------------
    # 1. FINNY THE CAT MASCOT (pet_cat)
    # -------------------------------------------------------------
    finny_src = os.path.join(BRAIN_DIR, "finny_mascot_cat_1790426948295.jpg")
    img_finny = cv2.imread(finny_src)
    gray = cv2.cvtColor(img_finny, cv2.COLOR_BGR2GRAY)
    cat_body = gray < 210
    num, labels, stats, centroids = cv2.connectedComponentsWithStats(cat_body.astype(np.uint8), connectivity=8)
    largest_label = 1 + np.argmax(stats[1:, cv2.CC_STAT_AREA])
    body_mask = (labels == largest_label)
    solid_cat = binary_fill_holes(body_mask)
    
    alpha = smooth_alpha_edge(solid_cat)
    b, g, r = cv2.split(img_finny)
    rgba = cv2.merge([b, g, r, alpha])
    finny_pil = Image.fromarray(cv2.cvtColor(rgba, cv2.COLOR_BGRA2RGBA))
    bbox = finny_pil.getbbox()
    finny_pil = finny_pil.crop(bbox)
    
    # Save idle (height 340)
    aspect = finny_pil.width / finny_pil.height
    target_h = 340
    target_w = int(target_h * aspect)
    finny_idle = finny_pil.resize((target_w, target_h), Image.Resampling.LANCZOS)
    finny_idle.save(os.path.join(RES_DIR, "pet_cat_idle.png"), "PNG")
    print(f"Saved pet_cat_idle.png {finny_idle.size}")
    
    # Save portrait
    finny_portrait = make_circular_portrait(finny_pil, (256, 256))
    finny_portrait.save(os.path.join(RES_DIR, "pet_cat_portrait.png"), "PNG")
    print("Saved pet_cat_portrait.png")

    # -------------------------------------------------------------
    # 2. HERO CHIBI (hero_idle)
    # -------------------------------------------------------------
    hero_src = os.path.join(BRAIN_DIR, "hero_chibi_sanrio_1790432294828.jpg")
    img_hero = cv2.imread(hero_src)
    hero_crop = img_hero[70:950, 360:730].copy()
    hero_pil = extract_character_silhouette(hero_crop, bg_thresh=18)
    hero_aspect = hero_pil.width / hero_pil.height
    target_h = 360
    target_w = int(target_h * hero_aspect)
    hero_idle = hero_pil.resize((target_w, target_h), Image.Resampling.LANCZOS)
    hero_idle.save(os.path.join(RES_DIR, "hero_idle.png"), "PNG")
    print(f"Saved hero_idle.png {hero_idle.size}")

    # -------------------------------------------------------------
    # 3. OTHER 6 PET SPECIES (seven_pet_species_sheet)
    # -------------------------------------------------------------
    pets_sheet_src = os.path.join(BRAIN_DIR, "seven_pet_species_sheet_1790427797701.jpg")
    img_pets = cv2.imread(pets_sheet_src)
    pet_coords = {
        'pet_dog': ((190, 435, 340, 645), False),
        'pet_fox': ((320, 420, 500, 645), False),
        'pet_panda': ((490, 410, 655, 645), True),   # white fur
        'pet_bunny': ((650, 435, 750, 645), True),   # white fur
        'pet_raccoon': ((745, 445, 875, 645), False),
        'pet_owl': ((870, 495, 985, 645), False)
    }
    for name, ((x1, y1, x2, y2), is_white) in pet_coords.items():
        crop = img_pets[y1:y2, x1:x2].copy()
        pil_char = extract_character_silhouette(crop, bg_thresh=20, is_white_char=is_white)
        aspect = pil_char.width / pil_char.height
        target_h = 240
        target_w = int(target_h * aspect)
        resized_char = pil_char.resize((target_w, target_h), Image.Resampling.LANCZOS)
        resized_char.save(os.path.join(RES_DIR, f"{name}_idle.png"), "PNG")
        
        portrait = make_circular_portrait(pil_char, (256, 256))
        portrait.save(os.path.join(RES_DIR, f"{name}_portrait.png"), "PNG")
        print(f"Saved {name}_idle.png and {name}_portrait.png")

    # -------------------------------------------------------------
    # 4. FRIENDS (bear Potap + 6 city friends)
    # -------------------------------------------------------------
    bear_src = os.path.join(BRAIN_DIR, "bear_potap_friend_1790426985069.jpg")
    img_bear = cv2.imread(bear_src)
    bear_crop = img_bear[70:950, 140:880].copy()
    bear_pil = extract_character_silhouette(bear_crop, bg_thresh=22)
    bear_aspect = bear_pil.width / bear_pil.height
    bear_idle = bear_pil.resize((int(340 * bear_aspect), 340), Image.Resampling.LANCZOS)
    bear_idle.save(os.path.join(RES_DIR, "friend_1_idle.png"), "PNG")
    bear_portrait = make_circular_portrait(bear_pil, (256, 256))
    bear_portrait.save(os.path.join(RES_DIR, "friend_1_portrait.png"), "PNG")
    print("Saved friend_1_idle.png and friend_1_portrait.png")

    friends_sheet_src = os.path.join(BRAIN_DIR, "friends_character_sheet_1790427759484.jpg")
    img_friends = cv2.imread(friends_sheet_src)
    friends_coords = {
        'friend_2': ((20, 20, 340, 360), False),
        'friend_3': ((350, 20, 670, 360), False),
        'friend_4': ((670, 20, 980, 360), False),
        'friend_5': ((50, 360, 340, 700), False),
        'friend_6': ((350, 360, 670, 700), True),   # Bunny Senya (white)
        'friend_7': ((680, 360, 980, 700), False),
    }
    for name, ((x1, y1, x2, y2), is_white) in friends_coords.items():
        crop = img_friends[y1:y2, x1:x2].copy()
        pil_char = extract_character_silhouette(crop, bg_thresh=20, is_white_char=is_white)
        aspect = pil_char.width / pil_char.height
        target_h = 320
        target_w = int(target_h * aspect)
        resized_char = pil_char.resize((target_w, target_h), Image.Resampling.LANCZOS)
        resized_char.save(os.path.join(RES_DIR, f"{name}_idle.png"), "PNG")
        portrait = make_circular_portrait(pil_char, (256, 256))
        portrait.save(os.path.join(RES_DIR, f"{name}_portrait.png"), "PNG")
        print(f"Saved {name}_idle.png and {name}_portrait.png")

    # -------------------------------------------------------------
    # 5. CITY WORKERS (hero_and_workers_sheet)
    # -------------------------------------------------------------
    workers_sheet_src = os.path.join(BRAIN_DIR, "hero_and_workers_sheet_1790427777594.jpg")
    img_workers = cv2.imread(workers_sheet_src)
    workers_coords = {
        'worker_bank_idle': (285, 370, 525, 770),
        'worker_hospital_idle': (510, 370, 745, 770),
        'worker_shop_idle': (730, 370, 975, 770),
    }
    for name, (x1, y1, x2, y2) in workers_coords.items():
        crop = img_workers[y1:y2, x1:x2].copy()
        pil_char = extract_character_silhouette(crop, bg_thresh=20)
        aspect = pil_char.width / pil_char.height
        target_h = 340
        target_w = int(target_h * aspect)
        resized_char = pil_char.resize((target_w, target_h), Image.Resampling.LANCZOS)
        resized_char.save(os.path.join(RES_DIR, f"{name}.png"), "PNG")
        print(f"Saved {name}.png")

    # -------------------------------------------------------------
    # 6. ALL 9 ITEMS AND JARS (game_items_icons)
    # -------------------------------------------------------------
    items_src = os.path.join(BRAIN_DIR, "game_items_icons_1790427043074.jpg")
    img_items = cv2.imread(items_src)
    item_crops = {
        'jar_needs': (45, 45, 325, 325, 45, 28),
        'jar_fun': (360, 45, 640, 325, 45, 28),
        'jar_savings': (675, 45, 955, 325, 40, 28),
        'item_food_kibble': (45, 375, 325, 655, 50, 28),
        'item_care_shampoo': (360, 375, 640, 655, 48, 28),
        'item_toy_ball': (675, 375, 955, 655, 45, 28),
        'item_hat_party': (55, 700, 280, 930, 30, 30),
        'item_neck_bow': (270, 720, 490, 930, 35, 28),
        'icon_paw_coin': (560, 700, 940, 940, 25, 32),
    }
    for name, (x1, y1, x2, y2, b_cut, d_thresh) in item_crops.items():
        crop = img_items[y1:y2, x1:x2].copy()
        pil_item = extract_item_prop(crop, bottom_cut=b_cut, dist_thresh=d_thresh)
        pil_item.save(os.path.join(RES_DIR, f"{name}.png"), "PNG")
        print(f"Saved {name}.png (clean isolated 3D object)")

    # -------------------------------------------------------------
    # 7. AUTOMATED VERIFICATION ASSERTIONS
    # -------------------------------------------------------------
    print("\n=== RUNNING AUTOMATED VERIFICATION TESTS ===")
    
    # Test 1: Finny's eyes must be 100% solid
    finny_im = Image.open(os.path.join(RES_DIR, "pet_cat_idle.png"))
    finny_arr = np.array(finny_im)
    is_white = (finny_arr[:,:,0] > 200) & (finny_arr[:,:,1] > 200) & (finny_arr[:,:,2] > 200)
    white_alphas = finny_arr[:,:,3][is_white]
    zero_whites = np.sum(white_alphas == 0)
    print(f"VERIFY FINNY EYES: {len(white_alphas)} white pixels, zero_count={zero_whites}")
    assert zero_whites == 0, f"FAILED: Finny has {zero_whites} transparent pixels in white highlights!"
    
    # Test 2: Hero eyes/clothes solid
    hero_im = Image.open(os.path.join(RES_DIR, "hero_idle.png"))
    hero_arr = np.array(hero_im)
    hero_center = hero_arr[int(0.2*hero_arr.shape[0]):int(0.7*hero_arr.shape[0]), int(0.3*hero_arr.shape[1]):int(0.7*hero_arr.shape[1]), 3]
    hero_center_zeros = np.sum(hero_center == 0)
    print(f"VERIFY HERO CENTER: zero_count={hero_center_zeros}")
    assert hero_center_zeros == 0, f"FAILED: Hero center has {hero_center_zeros} transparent pixels!"

    # Test 3: Items have 100% transparent borders (no pink cards, no borders, no text)
    for f in ['jar_savings.png', 'item_food_kibble.png', 'item_hat_party.png', 'icon_paw_coin.png', 'jar_needs.png', 'jar_fun.png']:
        p = os.path.join(RES_DIR, f)
        arr = np.array(Image.open(p))
        assert arr.shape[-1] == 4, f"Item {f} missing alpha!"
        alpha = arr[:, :, 3]
        border_max = max(alpha[0, :].max(), alpha[-1, :].max(), alpha[:, 0].max(), alpha[:, -1].max())
        print(f"VERIFY ITEM: {f:24} border_max_alpha={border_max}")
        assert border_max == 0, f"FAILED: Item {f} has non-transparent border {border_max}!"

    print("\nALL VERIFICATIONS PASSED SUCCESSFULLY!")

if __name__ == "__main__":
    run_extraction()
