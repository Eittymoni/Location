# Move Profile Header to a Navigation Drawer in FriendListActivity

The user wants to move the "My Profile" section from the top of the `FriendListActivity` into a navigation drawer. This will clean up the dashboard and provide a standard navigation pattern for profile-related info.

## User Review Required

> [!IMPORTANT]
> I will be adding a `Toolbar` to the `FriendListActivity` to provide a "hamburger" menu button to open the drawer. This will change the top UI of the screen slightly.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///D:/DIPTY/location_exam/gradle/libs.versions.toml)
- Add `drawerlayout` version and library.

#### [MODIFY] [app/build.gradle.kts](file:///D:/DIPTY/location_exam/app/build.gradle.kts)
- Add `androidx.drawerlayout` dependency.

### Resources & Layouts

#### [NEW] [nav_header_friend_list.xml](file:///D:/DIPTY/location_exam/app/src/main/res/layout/nav_header_friend_list.xml)
- Create a layout for the drawer header containing the user's name, email, and current location (extracted from the current CardView).

#### [MODIFY] [activity_friend_list.xml](file:///D:/DIPTY/location_exam/app/src/main/res/layout/activity_friend_list.xml)
- Wrap the existing `ConstraintLayout` in a `DrawerLayout`.
- Add a `Toolbar` at the top of the content area.
- Remove the `layoutMyProfile` CardView from the content area.
- Add a `NavigationView` with the new header.

### Code

#### [MODIFY] [FriendListActivity.kt](file:///D:/DIPTY/location_exam/app/src/main/java/com/zubayer/location_exam/FriendListActivity.kt)
- Initialize the `DrawerLayout` and `Toolbar`.
- Set up `ActionBarDrawerToggle`.
- Update `loadCurrentUser` to bind data to the `NavigationView` header instead of the old CardView.
- Remove redundant click listeners for the old CardView.

## Verification Plan

### Automated Tests
- Run Gradle Sync.
- Verify the build completes successfully.

### Manual Verification
- Open the app and navigate to the Friend List.
- Verify the hamburger menu is visible in the toolbar.
- Click the hamburger menu to open the drawer.
- Verify the profile information (Name, Email, Location) is correctly displayed in the drawer header.
- Verify the drawer can be closed.
