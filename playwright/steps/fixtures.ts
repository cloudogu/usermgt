import {ImportPage} from "./import_page";
import {createBdd, test as base} from "playwright-bdd";

type ImportPageFixture = {
    importPage: ImportPage;
};

export const test = base.extend<ImportPageFixture>({
                                                       // here you would also specify your own fixtures
                                                       importPage: async ({page}, use) => {
                                                           await use(new ImportPage(page));
                                                       },
                                                   });

export const {Given, When, Then} = createBdd(test);
export {expect} from '@playwright/test';
