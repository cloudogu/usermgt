import {Page} from "@playwright/test";
import {testUser} from './userdata.json'

const btoa = (str: string) => Buffer.from(str).toString('base64');
const credentialsBase64 = btoa(`${process.env.ADMIN_USERNAME}:${process.env.ADMIN_PASSWORD}`);

export const login = async (page: Page, username: string, password: string) => {
    await page.goto('/cas/login');
    await page.getByTestId("login-username-input-field").fill(username);
    await page.getByTestId("login-password-input-field").fill(password);
    await page.getByRole('button', {name: 'Login'}).click();
    await page.goto('/usermgt/account');
}

export const logout = async (page: Page) => {
    await page.request.get('/usermgt/api/logout', {
        failOnStatusCode: false,
        headers: {
            'Authorization': `Basic ${credentialsBase64}`
        }
    });
}

export const tryDeleteUser = async (page: Page, username: string) => {
    await page.request.delete('/usermgt/api/users/' + username, {
        failOnStatusCode: false,
        headers: {
            'Authorization': `Basic ${credentialsBase64}`
        }
    });
}

export const tryCreateUser = async (page: Page, username: string) => {
    let user: { username: string; givenname: string; surname: string; displayName: string; mail: string; password: string; };
    let currentUser: { username: any; givenname: any; surname: any; displayName: any; mail: any; password: any; };

    for (currentUser of testUser) {
        if (currentUser.username === username) {
            user = currentUser;
        }
    }
    await page.request.post('/usermgt/api/users', {
        data: {
            username: user.username,
            givenname: user.givenname,
            surname: user.surname,
            displayName: user.displayName,
            mail: user.mail,
            password: user.password,
            pwdReset: false,
            memberOf: [],
        },
        failOnStatusCode: false,
        headers: {
            'Authorization': `Basic ${credentialsBase64}`
        }
    });
}

export const tryDeleteGroup = async (page: Page, group: string) => {
    await page.request.delete('/usermgt/api/groups/' + group, {
        failOnStatusCode: false,
        headers: {
            'Authorization': `Basic ${credentialsBase64}`
        }
    });
}

export const createGroup = async (page: Page, group: string) => {
    await page.request.post('/usermgt/api/groups', {
        data: {
            name: group,
            description: "",
            members: []
        },
        failOnStatusCode: false,
        headers: {
            'Authorization': `Basic ${credentialsBase64}`
        }
    })
}

export const addMemberToGroup = async (page: Page, group: string, username: string) => {
    await page.request.post(`/usermgt/api/groups/${group}/members/${username}`, {
        failOnStatusCode: false,
        headers: {
            'Authorization': `Basic ${credentialsBase64}`
        }
    });
}
