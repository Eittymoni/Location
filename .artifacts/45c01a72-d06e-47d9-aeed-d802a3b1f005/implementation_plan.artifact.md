# Move Loading State to Buttons in AuthActivity

The user wants to remove the central `ProgressBar` and instead show the loading state directly on the "Sign In" and "Sign Up" buttons.

## Proposed Changes

### UI & Layout

#### [MODIFY] [activity_auth.xml](file:///D:/DIPTY/location_exam/app/src/main/res/layout/activity_auth.xml)
- Remove the `ProgressBar` element from the layout.

### Activity Logic

#### [MODIFY] [AuthActivity.kt](file:///D:/DIPTY/location_exam/app/src/main/java/com/zubayer/location_exam/AuthActivity.kt)
- Update the `observeViewModel` function to handle the `loading` state by:
    - Changing the text of `btnSubmitSignIn` to "Signing In..." and `btnSubmitSignUp` to "Signing Up..." when loading is active.
    - Restoring the original text ("Sign In" and "Sign Up") when loading is finished.
    - Continuing to disable the buttons while loading to prevent multiple submissions.

## Verification Plan

### Automated Tests
- Run Gradle Sync.
- Verify that `AuthActivity.kt` compiles successfully.

### Manual Verification
1. Open the app and navigate to the Sign In screen.
2. Enter credentials and click "Sign In".
3. Verify that the button text changes to "Signing In..." and the central progress bar is gone.
4. Verify that once the login succeeds or fails, the button text returns to "Sign In".
5. Repeat for the Sign Up screen.
