<p align="center">
  <img src="assets/logo.png" width="300">
</p>

# Sledge Launcher

The **Installer & Packager** for SledgeMC.

**Sledge Launcher** is the entry point for end-users, designed to prepare the Minecraft environment for SledgeMC with a focus on modern aesthetics and ease of use.

## Architecture

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   SledgeMC-API  │────▶│ SledgeMC-Loader │────▶│  SledgeMC-Agent │
│   (Core API)    │     │  (Mod Loading)  │     │ (Java Agent)    │
└─────────────────┘     └─────────────────┘     └─────────────────┘
                                │
                                ▼
                        ┌─────────────────┐
                        │    Launcher     │
                        │   (JavaFX UI)   │
                        └─────────────────┘
```

## Role
- Prepares the Minecraft environment for SledgeMC.
- Manages profile installation for various launchers.

## Features
- **Clean UI**: A modern, card-based interface with high-end dark aesthetics.
- **Offical Launcher Integration**: Patches and installs profiles directly into the `.minecraft/versions` folder.
- **Portable Support**: Exports specialized `.zip` instances crafted specifically for Prism/MultiMC etc..

**CONTACT:**
- Discord: [SledgeMC Community](https://discord.gg/astera)
- Example-MOD: [Example-MOD](https://github.com/Example-MOD)

## Build
To build the Launcher jar:
```bash
./gradlew clean build
```
**Note:** *Class purpose and authorship are documented in the headers of each class.*
