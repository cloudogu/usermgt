import {expect, Then} from "./fixtures";

Then('no content is displayed and upload is not possible', async ({page}) => {
    await expect(page.getByRole('table')).not.toBeVisible();
    await expect(page.getByTestId("upload-button")).toBeVisible();
    await expect(page.getByTestId("upload-button")).toBeDisabled();
    await expect(page.getByTestId("reset-button")).toBeVisible();
    await expect(page.getByTestId("reset-button")).toBeDisabled();
});

Then('the user import page is shown', async ({page}) => {
    await expect(page.getByRole("heading", {level: 1})).toHaveText('User import');
    await expect(page.getByTestId('userImport-input')).toBeVisible();
    await expect(page.getByTestId('upload-button')).toBeVisible();
    await expect(page.getByTestId('upload-button')).toBeDisabled();
    await expect(page.getByTestId('reset-button')).toBeVisible();
    await expect(page.getByTestId('reset-button')).toBeDisabled();
});


Then('users, groups, user import and import overview should be visible in the navbar', async ({page}) => {
    await expect(page.getByTestId("cloudogu-navbar-li-/users")).toBeVisible();
    await expect(page.getByTestId("cloudogu-navbar-li-/groups")).toBeVisible();
    await expect(page.getByTestId("cloudogu-navbar-li-/users/import")).toBeVisible();
    await expect(page.getByTestId("cloudogu-navbar-li-/summaries")).toBeVisible();
})


Then('the account page for user {string} is shown', async ({page}, username) => {
    await expect(page.getByRole("heading", {level: 1})).toHaveText('My Account');
    await expect(page.getByTestId("username-input")).toHaveValue(username);
});
