import {When} from "./fixtures";

When('the user opens the user import page', async ({importPage}) => {
    await importPage.goto();
});

When('the user selects the file {string}', async ({importPage}, file) => {
    await importPage.selectFile( file);
});

