import {Page} from "@playwright/test";
import {testUser} from './userdata.json'

export const login = async (page: Page, username: string, password: string) => {
    await page.goto('/cas/login');
    await page.getByTestId("login-username-input-field").fill(username);
    await page.getByTestId("login-password-input-field").fill(password);
    await page.getByRole('button', {name: 'Login'}).click();
    await page.goto('/usermgt/account');
}

export const tryDeleteUser = async (page: Page, username: string) => {
    await page.request.delete('/usermgt/api/users/' + username, {failOnStatusCode: false});
}

export const tryCreateUser = async (page: Page, username: string) => {
    let user: { username: string; givenname: string; surname: string; displayName: string; mail: string; password: string; };

    for(user of testUser) {
        if(user.username === username){
            return user;
        }
    }
    await page.request.post('/usermgt/api/users/' + username, {
        data: {
            username: user.username,
            givenname: user.givenname,
            surname: user.surname,
            displayName: user.displayName,
            mail: user.mail,
            password: user.password,
            pwdReset: null,
            memberOf: [],
        },
        failOnStatusCode: false,
    });
}

export const tryDeleteGroup = async (page: Page, group: string) => {
    await page.request.delete('/usermgt/api/groups/' + group, {failOnStatusCode: false});
}

export const createGroup = async (page: Page, group: string) => {
    await page.request.post('/usermgt/api/groups', {
        data: {
            name: group,
            description: "",
            members: []
        },
        failOnStatusCode: false,
    })
}

export const addMemberToGroup = async (page: Page, group: string, username: string) => {
    await page.request.post(`/usermgt/api/groups/${group}/members/${username}`, {failOnStatusCode: false});
}
