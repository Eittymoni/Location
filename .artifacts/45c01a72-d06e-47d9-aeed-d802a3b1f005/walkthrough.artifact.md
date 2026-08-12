# Walkthrough - Loading State on Buttons

I have updated the authentication flow to show the loading status directly on the action buttons instead of using a separate progress bar.

## Changes Made

### UI Enhancements
- **[activity_auth.xml](file:///D:/DIPTY/location_exam/app/src/main/res/layout/activity_auth.xml)**: Removed the central `ProgressBar`.
- **[AuthActivity.kt](file:///D:/DIPTY/location_exam/app/src/main/java/com/zubayer/location_exam/AuthActivity.kt)**:
    - Updated the `loading` state observer.
    - When loading begins, the button text changes to **"Signing In..."** or **"Signing Up..."**.
    - When loading finishes, the text reverts to **"Sign In"** or **"Sign Up"**.
    - Buttons remain disabled during the process to prevent accidental double-clicks.

## Verification Results
- **Gradle Sync**: Successful.
- **Button Behavior**: The UI now provides immediate feedback on the button itself, resulting in a cleaner and more modern look.
