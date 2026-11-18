# Replica Studio 4.0 – Android alpha 1

Native Kotlin / Jetpack Compose Android prototype. The existing GitHub Pages FacePrint AI remains untouched on main.

## Implemented
- Android app shell with local image selection (reference and target)
- Touch-driven 3D parametric wireframe human avatar: orbit by drag, zoom slider, editable shoulders, waist, hips, bust, height
- Clothing visualization toggle (technical base mannequin vs clothed silhouette, **not** a realistic nude reconstruction)
- Editable English Secrets AI prompt and 500-character counter / copy
- GitHub Actions debug APK artifact on the feature branch

## Not yet implemented
- Photo-to-3D reconstruction, AI-based comparison, segmentation, skeletal rigging, photorealistic rendering, wardrobe asset library, cloud API integration, production signing, OTA.
- No claim of recovering real height, weight, or unseen anatomy from photos.

## Build
Run GitHub Actions > Replica Studio Android APK, download artifact and install APK. Debug builds are for testing only.

## Alpha 2 – Photo-to-avatar first fit
- Reference photo is processed on device using ML Kit Pose Detection.
- Left/right shoulder and hip landmarks are required with minimum confidence.
- Shoulder-to-hip width ratio initializes the parametric avatar's shoulder slider.
- This is a **2D landmark-based proportion estimate**, not 3D photogrammetry, photorealistic body scanning, weight estimation, or reliable reconstruction of occluded geometry.
- Next: full pose rigging, segmentation, multi-view calibration, optional secure cloud mesh generation, glTF model viewer.
