# JetpackGlobe 🌍

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84.svg?style=flat&logo=android)](https://www.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202025+-4285F4.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)
[![JitPack](https://jitpack.io/v/wailbabou/JetpackGlobe.svg)](https://jitpack.io/#wailbabou/JetpackGlobe)
[![GitHub release](https://img.shields.io/github/v/release/wailbabou/JetpackGlobe?label=latest)](https://github.com/wailbabou/JetpackGlobe/releases)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

An interactive, GPU-accelerated **3D Globe component for Jetpack Compose**. Built from first mathematical principles without heavy OpenGL/3D engine dependencies, providing smooth gestures, automatic shortest-path camera centering, item selection with depth perspective, and background blur effects.



https://github.com/user-attachments/assets/2a6a8fa3-f8bd-401f-9838-60362db51007



---

## ✨ Features

- 🌐 **Pure Jetpack Compose**: Built 100% in Kotlin & Compose using Canvas and standard 3D projection math.
- 📐 **Fibonacci Sphere Distribution**: Generates perfectly equidistant 3D coordinates for any number of nodes without clustering at poles.
- 🎯 **Shortest-Path Auto-Centering**: Tapping any node smoothly rotates the sphere along the shortest geodesic path to face the front camera.
- 🔍 **Depth Perspective & Dynamic Blur**:
  - Front nodes appear larger and fully opaque (`alpha = 1.0`).
  - Distant rear nodes scale down with depth attenuation (`alpha = 0.15`).
  - Non-selected background nodes smoothly blur when a node is selected.
  - Z-indexing ensures front & selected items are always layered correctly.
- 🔄 **Continuous Auto-Rotation**: Smooth frame-nanosecond timed rotation that seamlessly pauses during drag gestures or node inspection.
- 👆 **Interactive Drag Gestures**: 2-axis (yaw and pitch) touch rotation with velocity sensitivity.
- 🧩 **Generic Slot API**: Fully decoupled from your data model. Render avatars, country flags, glowing particles, badges, or custom views.
- 🏗️ **Clean Architecture & Gradle Convention Plugins**: Standardized modular structure (`build-logic`, `:globe` library, and `:app` sample).

---

## 🧠 How It Works

### 1. Fibonacci Sphere Lattice
To distribute $N$ items evenly across a 3D unit sphere, we use the spherical Fibonacci spiral:
$$\phi = \arccos\left(1 - \frac{2(i + 0.5)}{N}\right)$$
$$\theta = \pi \cdot (1 + \sqrt{5}) \cdot i$$
$$x = \sin\phi \cos\theta, \quad y = \cos\phi, \quad z = \sin\phi \sin\theta$$

### 2. 3D-to-2D Projection & Camera Angles
Given pitch rotation angle $\alpha$ and yaw rotation angle $\beta$:
1. **Pitch (X-Axis)**:
   $$y' = y \cos\alpha - z \sin\alpha, \quad z' = y \sin\alpha + z \cos\alpha$$
2. **Yaw (Y-Axis)**:
   $$x'' = x \cos\beta + z' \sin\beta, \quad z'' = -x \sin\beta + z' \cos\beta$$
3. **Screen Projection**:
   $$\text{screenX} = x'' \cdot R, \quad \text{screenY} = y' \cdot R, \quad \text{screenZ} = z'' \cdot R$$
   $$\text{scale} = 0.55 + 0.45 \cdot \text{normalizedZ}$$

### 3. Geodesic Shortest-Path Centering
When a user selects a node at $(x_0, y_0, z_0)$, the camera targets:
$$\text{targetRotX} = \text{atan2}(y_0, z_0)$$
$$\text{targetRotY} = \text{atan2}(-x_0, z_1)$$
The angle is normalized using minimal modulo differences ($[-180^\circ, +180^\circ]$) to ensure the globe never rotates the long way around.

---

## 📦 Installation

### Step 1 — Add JitPack repository

In your root `settings.gradle.kts`:
```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Step 2 — Add the dependency

**Version Catalog (`libs.versions.toml`)**:
```toml
[versions]
jetpackGlobe = "0.1"

[libraries]
jetpack-globe = { module = "com.github.wailbabou:JetpackGlobe", version.ref = "jetpackGlobe" }
```
```kotlin
// build.gradle.kts
dependencies {
    implementation(libs.jetpack.globe)
}
```

**Without Version Catalog**:
```kotlin
dependencies {
    implementation("com.github.wailbabou:JetpackGlobe:1.0.0")
}
```

---

## 🚀 Quickstart

### 1. Define your data model
Implement the `GlobePoint` interface:

```kotlin
import com.ouail.globe.model.GlobePoint

data class UserNode(
    override val id: String,
    val name: String,
    val avatarUrl: String
) : GlobePoint
```

### 2. Render `GlobeView` in Compose
```kotlin
@Composable
fun MyGlobeScreen() {
    val nodes = remember {
        listOf(
            UserNode("1", "Sophia", "https://..."),
            UserNode("2", "Liam", "https://..."),
            UserNode("3", "Amara", "https://...")
        )
    }

    var selectedNode by remember { mutableStateOf<UserNode?>(null) }

    GlobeView(
        items = nodes,
        selectedItem = selectedNode,
        onItemSelected = { selectedNode = it },
        modifier = Modifier.fillMaxWidth()
    ) { node, isSelected, normalizedZ ->
        // Custom composable for each node
        Box(
            modifier = Modifier
                .size(if (isSelected) 80.dp else 56.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color.Black else Color.White)
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = node.avatarUrl,
                contentDescription = node.name,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
```

---

## 🛠️ Advanced Configuration

Customize rotation speeds, blur intensities, zoom factors, and sensitivity using `GlobeConfig`:

```kotlin
val customConfig = GlobeConfig(
    autoRotationDurationMs = 18000,    // 18s per 360° spin
    isAutoRotationEnabled = true,      // Set to false to disable auto-spin
    selectedZoomScale = 1.6f,          // 160% scale on selection
    unselectedBlurRadius = 8.dp,       // Blur background nodes on selection
    minDepthAlpha = 0.12f,             // Min opacity for rear nodes
    dragSensitivity = 0.25f,           // Gesture rotation speed
    centeringDurationMs = 800,         // Smooth spring animation duration
    paddingDp = 40.dp                  // Outer padding from canvas edge
)

GlobeView(
    items = users,
    selectedItem = selectedUser,
    onItemSelected = { selectedUser = it },
    config = customConfig,
    showWireframe = true,
    wireframeColor = Color.Black.copy(alpha = 0.08f),
    centerGlowColor = Color.Black.copy(alpha = 0.03f)
) { user, isSelected, normalizedZ ->
    SampleGlobeItem(user = user, isSelected = isSelected)
}
```

---

## 📖 API Reference

### `GlobeView` Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `items` | `List<T : GlobePoint>` | *Required* | List of items to display on the 3D sphere. |
| `selectedItem` | `T?` | `null` | The currently selected/focused item. |
| `onItemSelected` | `(T?) -> Unit` | `{}` | Callback when a node is tapped (or `null` on deselect). |
| `config` | `GlobeConfig` | `GlobeDefaults.DefaultConfig` | Configuration for physics, animations, and blur. |
| `showWireframe` | `Boolean` | `true` | Whether to draw ambient sphere meridian/equator lines. |
| `wireframeColor` | `Color` | `Color.White.copy(0.15f)` | Stroke color for the sphere outline. |
| `centerGlowColor` | `Color` | `Color.White.copy(0.06f)` | Radial gradient color for the core sphere aura. |
| `itemContent` | `@Composable (item: T, isSelected: Boolean, normalizedZ: Float) -> Unit` | *Required* | Slot to render each node's UI. |

---

## 🏛️ Project Architecture

```
JetpackGlobe/
├── build-logic/                       # Gradle convention plugins
│   └── convention/src/main/kotlin/
│       ├── AndroidApplicationConventionPlugin.kt
│       ├── AndroidLibraryConventionPlugin.kt
│       ├── AndroidLibraryComposeConventionPlugin.kt
│       └── AndroidLibraryPublishConventionPlugin.kt  # 📦 maven-publish (JitPack)
├── globe/                             # 📦 Standalone Library Module
│   └── src/main/kotlin/com/ouail/globe/
│       ├── GlobeView.kt               # Core 3D Globe Composable
│       ├── GlobeDefaults.kt           # GlobeConfig & parameters
│       ├── math/GlobeMath.kt          # Fibonacci & 3D projection algorithms
│       └── model/GlobeItem.kt         # GlobePoint interface
└── app/                               # 📱 Sample Showcase Application
    └── src/main/kotlin/com/ouail/jetpackglobe/
        ├── MainActivity.kt
        ├── ui/GlobeShowcaseScreen.kt
        ├── ui/SampleGlobeItem.kt
        └── data/SampleDataProvider.kt
```

---

## 🙏 Credits

This library was inspired by the globe visualization concept from [**compose_concepts**](https://github.com/pedromassango/compose_concepts) by [@pedromassango](https://github.com/pedromassango). Big thanks for the creative foundation!

---

## 📄 License

```
MIT License

Copyright (c) 2026 Ouail Bellal

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```
