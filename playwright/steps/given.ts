import {Given} from "./fixtures";
import {addMemberToGroup, createGroup, login, logout, tryCreateUser, tryDeleteGroup, tryDeleteUser} from "./utils";

Given('the user {string} with password {string} is logged in', async ({page}, username, password) => {
    await logout(page);
    await login(page, username, password);
})

Given('the user {string} exists', async ({page}, username) => {
    await tryDeleteUser(page, username);
    await tryCreateUser(page, username);
})

Given('the user {string} is member of the group {string}', async ({page}, username, group) => {
    await addMemberToGroup(page, group, username);
})
