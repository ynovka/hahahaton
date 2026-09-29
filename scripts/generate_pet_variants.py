"""Создаёт временные полосатые/пятнистые варианты существующих PNG питомцев.

Скрипт не меняет оригиналы: варианты сохраняются рядом с ними как
pet_<вид>_spotted_v2.png. Когда художник передаст готовые окрасы, эти файлы
можно просто заменить, а игровая логика останется той же.
"""

from pathlib import Path
from PIL import Image, ImageChops, ImageDraw


ROOT = Path(__file__).resolve().parents[1] / "public" / "assets" / "characters"
SPECIES = ("cat", "dog", "fox", "panda", "bunny", "raccoon", "owl")


def make_variant(species: str) -> None:
    source = ROOT / f"pet_{species}_v2.png"
    target = ROOT / f"pet_{species}_spotted_v2.png"
    image = Image.open(source).convert("RGBA")
    overlay = Image.new("RGBA", image.size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(overlay)
    width, height = image.size

    # Полосы идут по корпусу и лапам, не закрывая глаза и мордочку.
    stripe = (103, 56, 31, 92)
    for offset in range(-width, width * 2, 78):
        draw.line(
            [(offset, int(height * .48)), (offset + int(width * .42), height)],
            fill=stripe,
            width=22,
        )

    # Несколько мягких пятен создают второй понятный тип окраса.
    spot = (87, 45, 27, 102)
    spots = (
        (.24, .53, .10),
        (.70, .57, .12),
        (.38, .78, .09),
        (.67, .82, .08),
    )
    for x, y, radius in spots:
        cx, cy = int(width * x), int(height * y)
        r = int(width * radius)
        draw.ellipse((cx - r, cy - r, cx + r, cy + r), fill=spot)

    # Прозрачная маска оригинала не даёт узору выйти за контур питомца.
    clipped_alpha = ImageChops.multiply(overlay.getchannel("A"), image.getchannel("A"))
    overlay.putalpha(clipped_alpha)
    Image.alpha_composite(image, overlay).save(target, optimize=True)


if __name__ == "__main__":
    for pet_species in SPECIES:
        make_variant(pet_species)
    print(f"Created {len(SPECIES)} pattern variants in {ROOT}")
