import type {Locator, Page} from '@playwright/test';
import path from "path";

export class ImportPage {
    private readonly inputBox: Locator;
    private readonly saveButton: Locator;

    constructor(public readonly page: Page) {
        this.inputBox = this.page.getByTestId('userImport-input');
        this.saveButton = this.page.getByTestId('upload-button');
    }

    async goto() {
        await this.page.goto('/usermgt/users/import');
    }

    async selectFile(file: string) {
        await this.inputBox.click();
        await this.inputBox.setInputFiles(path.join(__dirname, file));
    };

    async uploadFile(file: string) {
        await this.inputBox.click();
        await this.inputBox.setInputFiles(path.join(__dirname, file));
        await this.saveButton.click();
    };

}
