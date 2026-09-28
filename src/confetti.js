// ============================================================================
// Праздничный салют из конфетти на Canvas
// ============================================================================

export function fireConfetti(options = {}) {
  const count = options.count || 70;
  const canvas = document.getElementById('confetti-canvas');
  if (!canvas) return;

  const ctx = canvas.getContext('2d');
  canvas.width = window.innerWidth;
  canvas.height = window.innerHeight;

  const colors = ['#FF6B81', '#FF9F43', '#FECA57', '#1DD1A1', '#54A0FF', '#9B59B6', '#FF9FF3'];
  const particles = [];

  for (let i = 0; i < count; i++) {
    particles.push({
      x: canvas.width / 2 + (Math.random() - 0.5) * 100,
      y: canvas.height * 0.45 + (Math.random() - 0.5) * 60,
      vx: (Math.random() - 0.5) * 16,
      vy: (Math.random() - 0.8) * 18,
      size: Math.random() * 8 + 6,
      color: colors[Math.floor(Math.random() * colors.length)],
      rotation: Math.random() * 360,
      vRot: (Math.random() - 0.5) * 12,
      shape: Math.random() > 0.4 ? 'circle' : 'rect',
      alpha: 1
    });
  }

  let animationFrameId;
  function update() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    let activeCount = 0;

    for (const p of particles) {
      p.x += p.vx;
      p.y += p.vy;
      p.vy += 0.38; // гравитация
      p.vx *= 0.98; // сопротивление воздуха
      p.rotation += p.vRot;
      p.alpha -= 0.009;

      if (p.alpha > 0 && p.y < canvas.height + 20) {
        activeCount++;
        ctx.save();
        ctx.globalAlpha = Math.max(0, p.alpha);
        ctx.translate(p.x, p.y);
        ctx.rotate((p.rotation * Math.PI) / 180);
        ctx.fillStyle = p.color;

        if (p.shape === 'circle') {
          ctx.beginPath();
          ctx.arc(0, 0, p.size / 2, 0, Math.PI * 2);
          ctx.fill();
        } else {
          ctx.fillRect(-p.size / 2, -p.size / 2, p.size, p.size * 0.6);
        }
        ctx.restore();
      }
    }

    if (activeCount > 0) {
      animationFrameId = requestAnimationFrame(update);
    } else {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      cancelAnimationFrame(animationFrameId);
    }
  }

  cancelAnimationFrame(animationFrameId);
  update();
}
