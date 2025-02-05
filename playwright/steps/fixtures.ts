import {ImportPage} from "./import_page";
import {createBdd, test as base} from "playwright-bdd";
import {request} from "@playwright/test";
import path from "path";
import fs from 'fs';

type ImportPageFixture = {
    importPage: ImportPage;
};

type WorkerFixture = {
    workerStorageState: string;
}

export const test = base.extend<ImportPageFixture, WorkerFixture>({
                                    // here you would also specify your own fixtures
                                    importPage: async ({page}, use) => {
                                        await use(new ImportPage(page));
                                    },

                                    // Use the same storage state for all tests in this worker.
                                    storageState: ({workerStorageState}, use) => use(workerStorageState),

                                    // Authenticate once per worker with a worker-scoped fixture.
                                    workerStorageState:
                                        [async ({}, use) => {
                                            // Use parallelIndex as a unique identifier for each worker.
                                            const id = test.info().parallelIndex;
                                            const baseURL = test.info().project.use.baseURL
                                            const fileName = path.resolve(test.info().project.outputDir, `.auth/${id}.json`);

                                            if (fs.existsSync(fileName)) {
                                                // Reuse existing authentication state if any.
                                                await use(fileName);
                                                return;
                                            }

                                            // Important: make sure we authenticate in a clean environment by unsetting storage state.
                                            const context = await request.newContext({storageState: undefined});

                                            // Acquire a unique account, for example create a new one.
                                            // Alternatively, you can have a list of precreated accounts for testing.
                                            // Make sure that accounts are unique, so that multiple team members
                                            // can run tests at the same time without interference.
                                            //const account = await acquireAccount(id);

                                            // Send authentication request.
                                            await context.post(baseURL + `/cas/login`, {
                                                form: {
                                                    'user': process.env.ADMIN_USERNAME,
                                                    'password': process.env.ADMIN_PASSWORD
                                                }
                                            });

                                            await context.storageState({path: fileName});
                                            await context.dispose();
                                            await use(fileName);
                                        }, {scope: 'worker'}],
                                })
;

export const {Given, When, Then} = createBdd(test);
export {expect} from '@playwright/test';
