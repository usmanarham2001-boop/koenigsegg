"""Regenerates the textures and sounds. Needs numpy, Pillow and ffmpeg (with libvorbis)."""
import os, subprocess, tempfile
import numpy as np
from PIL import Image
import wave

ROOT = os.path.join(os.path.dirname(__file__), "..", "src", "main", "resources", "assets", "cars")
TEX = os.path.join(ROOT, "textures", "entity")
SND = os.path.join(ROOT, "sounds")
os.makedirs(TEX, exist_ok=True); os.makedirs(SND, exist_ok=True)
rng = np.random.default_rng(7)

def save(name, gray):
    g = np.clip(gray, 0, 255).astype(np.uint8)
    rgba = np.stack([g, g, g, np.full_like(g, 255)], axis=-1)
    Image.fromarray(rgba, "RGBA").save(os.path.join(TEX, name))

N = 128
# paint: bright, with fine metallic flake
paint = 238 + rng.normal(0, 4, (N, N))
flake = rng.random((N, N)) > 0.985
paint[flake] = 255
save("paint.png", paint)

# carbon fibre: 2x2 twill weave
y, x = np.mgrid[0:N, 0:N]
cell = ((x // 2) + (y // 2)) % 2
carbon = np.where(cell == 0, 150.0, 215.0) + rng.normal(0, 5, (N, N))
carbon += 12 * np.sin((x + y) * np.pi / 2)
save("carbon.png", carbon)

# leather: soft pebbled grain
g = rng.normal(0, 1, (N, N))
g = (g + np.roll(g, 1, 0) + np.roll(g, 1, 1) + np.roll(np.roll(g, 1, 0), 1, 1)) / 4
save("leather.png", 228 + g * 38)

# plain white (glass, lights, tires, rims)
save("plain.png", np.full((N, N), 255.0))

# ------------------------------------------------------------------ sounds
SR = 44100
def to_ogg(samples, name):
    s = np.clip(samples, -1, 1)
    pcm = (s * 32767).astype("<i2")
    with tempfile.TemporaryDirectory() as d:
        wav = os.path.join(d, "a.wav")
        with wave.open(wav, "wb") as w:
            w.setnchannels(1); w.setsampwidth(2); w.setframerate(SR); w.writeframes(pcm.tobytes())
        subprocess.run(["ffmpeg", "-y", "-loglevel", "error", "-i", wav, "-c:a", "libvorbis",
                        "-q:a", "5", "-ac", "1", os.path.join(SND, name)], check=True)

# engine loop: exactly 2 s long, every component has an integer number of cycles -> seamless loop
dur = 2.0
n = int(SR * dur)
t = np.arange(n) / SR
base = 36.0                                   # firing fundamental (Hz)
eng = np.zeros(n)
for k in range(1, 30):
    amp = 1.0 / (k ** 0.85)
    eng += amp * np.sin(2 * np.pi * base * k * t + rng.uniform(0, 2 * np.pi))
eng *= 1.0 + 0.45 * np.sin(2 * np.pi * 18 * t)          # cylinder pulsing
eng *= 1.0 + 0.15 * np.sin(2 * np.pi * 5 * t + 1.0)      # slow wobble
# periodic low rumble noise
spec = np.fft.rfft(rng.normal(0, 1, n))
f = np.fft.rfftfreq(n, 1 / SR)
spec *= 1.0 / (1.0 + (f / 250.0) ** 2)
rumble = np.fft.irfft(spec, n)
rumble /= np.max(np.abs(rumble))
sig = eng / np.max(np.abs(eng)) * 0.75 + rumble * 0.35
sig = np.tanh(2.2 * sig)                                  # growl / distortion
sig = sig / np.max(np.abs(sig)) * 0.85
to_ogg(sig, "engine_loop.ogg")

# horn: dual tone, slightly buzzy, with quick attack/release
dur = 1.2
n = int(SR * dur)
t = np.arange(n) / SR
def tone(fr):
    s = np.zeros(n)
    for k, a in zip(range(1, 10, 2), (1.0, 0.33, 0.2, 0.14, 0.11)):
        s += a * np.sin(2 * np.pi * fr * k * t)
    return s
horn = tone(415.0) + tone(520.0)
env = np.minimum(1, t / 0.02) * np.minimum(1, (dur - t) / 0.12)
horn = np.tanh(1.6 * horn / np.max(np.abs(horn))) * env * 0.85
to_ogg(horn, "horn.ogg")
print("assets generated")
