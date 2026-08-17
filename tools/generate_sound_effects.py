#!/usr/bin/env python3
"""Generate the original, lightweight AventuMapa UI chimes."""

import math
import struct
import wave
from pathlib import Path

SAMPLE_RATE = 44100


def envelope(index, total):
    attack = min(1.0, index / max(1, int(total * 0.08)))
    release = min(1.0, (total - index) / max(1, int(total * 0.20)))
    return attack * release


def tone(frequency, duration, volume=0.35, end_frequency=None):
    total = int(SAMPLE_RATE * duration)
    end_frequency = end_frequency or frequency
    phase = 0.0
    samples = []
    for index in range(total):
        progress = index / max(1, total - 1)
        current = frequency + (end_frequency - frequency) * progress
        phase += 2.0 * math.pi * current / SAMPLE_RATE
        value = math.sin(phase) * volume * envelope(index, total)
        samples.append(value)
    return samples


def silence(duration):
    return [0.0] * int(SAMPLE_RATE * duration)


def mix(*tracks):
    length = max(map(len, tracks))
    output = [0.0] * length
    for track in tracks:
        for index, value in enumerate(track):
            output[index] += value
    return [max(-1.0, min(1.0, value)) for value in output]


def sequence(*parts):
    output = []
    for part in parts:
        output.extend(part)
    return output


def save(path, samples):
    with wave.open(str(path), "wb") as audio:
        audio.setnchannels(1)
        audio.setsampwidth(2)
        audio.setframerate(SAMPLE_RATE)
        audio.writeframes(b"".join(struct.pack("<h", int(value * 32767)) for value in samples))


def main():
    destination = Path("app/src/main/res/raw")
    destination.mkdir(parents=True, exist_ok=True)
    save(destination / "aventu_flip.wav", mix(tone(520, 0.12, 0.24, 920), tone(1040, 0.09, 0.08, 1480)))
    save(
        destination / "aventu_success.wav",
        sequence(tone(523.25, 0.14), tone(659.25, 0.14), tone(783.99, 0.24, 0.40)),
    )
    save(
        destination / "aventu_try_again.wav",
        sequence(tone(440.0, 0.13, 0.18), silence(0.025), tone(392.0, 0.18, 0.16)),
    )
    save(
        destination / "aventu_match.wav",
        mix(
            sequence(tone(659.25, 0.12, 0.24), tone(987.77, 0.24, 0.30)),
            sequence(silence(0.10), tone(1318.51, 0.24, 0.12)),
        ),
    )


if __name__ == "__main__":
    main()
