import i18n from "i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import {initReactI18next} from "react-i18next";

const user = {
    en: {
        username: "Username",
        givenName: "Given Name",
        surname: "Surname",
        displayName: "Display Name",
        email: "E-Mail",
        password: "Password",
        confirmPassword: "Confirm Password"
    },
    de: {
        username: "Nutzername",
        givenName: "Vorname",
        surname: "Nachname",
        displayName: "Anzeigename",
        email: "E-Mail",
        password: "Passwort",
        confirmPassword: "Passwort bestätigen"
    }
};

const group = {
    en: {
        name: "Name",
        description: "Description",
    },
    de: {
        name: "Name",
        description: "Beschreibung",
    }
};

i18n
    // detect user language
    // learn more: https://github.com/i18next/i18next-browser-languageDetector
    .use(LanguageDetector)
    // pass the i18n instance to react-i18next.
    .use(initReactI18next)
    // init i18next
    // for all options read: https://www.i18next.com/overview/configuration-options
    .init({
        debug: true, fallbackLng: "en", interpolation: {
            escapeValue: false, // not needed for react as it escapes by default
        }, resources: {
            en: {
                translation: {
                    pages: {
                        account: "Account",
                        users: "Users",
                        groups: "Groups",
                        groupsNew: "New Group",
                        groupsEdit: "Edit group",
                    },
                    navbar: {
                        logout: "Logout",
                        logoAltText: "User Management icon: magnifying glass directed to a user icon.",
                    },
                    users: {
                        create: "Create user",
                        table: {
                            username: user.en.username,
                            displayName: user.en.displayName,
                            email: user.en.email,
                            actions: {
                                edit: "Edit user",
                                editAria: "edit",
                                delete: "Remove user",
                                deleteAria: "remove"
                            }
                        },
                        notification: {
                            success: "The user '{{username}}' was deleted successfully.",
                            error: "The user '{{username}}' could not be deleted."
                        },
                        confirmation: {
                            title: "Remove user",
                            message: "Remove user {{username}}?"
                        },
                    },
                    groups: {
                        create: "Create group",
                        table: {
                            name: group.en.name,
                            description: group.en.description,
                            users: "Members",
                            systemGroup: "This is a system group and cannot be deleted.",
                            actions: {
                                edit: "Edit group",
                                editAria: "edit",
                                delete: "Remove group",
                                deleteAria: "remove"
                            }
                        },
                        notification: {
                            success: "The group '{{groupName}}' was deleted successfully.",
                            error: "The group '{{groupName}}' could not be deleted."
                        },
                        confirmation: {
                            title: "Remove group",
                            message: "Remove group {{groupName}}?"
                        },
                    },
                    editGroup: {
                        labels: {
                            name: group.en.name,
                            description: group.en.description,
                        },
                        buttons: {
                            save: "Save",
                            back: "Back",
                            remove: "Remove",
                        },
                        notification: {
                            success: "The group was saved successfully.",
                            error: "The group could not be saved. Please try again later.",
                        }
                    },
                    newGroup: {
                        labels: {
                            name: group.de.name,
                            description: group.de.description,
                        },
                        buttons: {
                            save: "Save",
                            back: "Back"
                        },
                        errors: {
                            name: "Name is required.",
                            nameLength: "Name must have at least 2 characters.",
                        },
                        notification: {
                            success: "The group was saved successfully.",
                            error: "The group could not be created. Please try again later.",
                        },
                    },
                    editUser: {
                        labels: {
                            username: user.en.username,
                            givenName: user.en.givenName,
                            surname: user.en.surname,
                            displayName: user.en.displayName,
                            email: user.en.email,
                            password: user.en.password,
                            confirmPassword: user.en.confirmPassword
                        },
                        buttons: {
                            save: "Save"
                        },
                        alerts: {
                            success: "Account information saved successfully.",
                            error: "Account information could not be saved. Please try again later.",
                        },
                        errors: {
                            surname: "Surname is required.",
                            displayName: "Display name is required.",
                            email: {
                                invalid: "E-mail address is invalid.",
                                required: "'E-mail address is required.'",
                            },
                            confirmPassword: "Passwords must match.",
                            password: {
                                length: "The password must contain at least {{length}} characters.",
                                capital: "The password must contain at least one capital letter.",
                                lowercase: "The password must contain at least one lower case letter.",
                                numeric: "The password must contain at least 1 number.",
                                special: "The password must contain at least 1 special character.",
                            }
                        },
                    },
                    modal: {
                        confirm: "Confirm",
                        cancel: "Cancel"
                    }
                }
            },
            de: {
                translation: {
                    pages: {
                        account: "Account",
                        users: "Nutzer",
                        groups: "Gruppen",
                        groupsNew: "Neue Gruppe",
                        groupsEdit: "Gruppe bearbeiten",
                    },
                    navbar: {
                        logout: "Abmelden",
                        logoAltText: "Icon des User Managements: Lupe, die auf einen Benutzer-Icon gerichtet ist.",
                    },
                    users: {
                        create: "Nutzer anlegen",
                        table: {
                            username: user.de.username,
                            displayName: user.de.displayName,
                            email: user.de.email,
                            actions: {
                                edit: "Nutzer bearbeiten",
                                editAria: "bearbeiten",
                                delete: "Nutzer entfernen",
                                deleteAria: "entfernen"
                            }
                        },
                        notification: {
                            success: "Der Nutzer '{{username}}' wurde erfolgreich gelöscht.",
                            error: "Der Nutzer '{{username}}' konnte nicht gelöscht werden."
                        },
                        confirmation: {
                            title: "Nutzer entfernen",
                            message: "Soll der Nutzer {{username}} entfernt werden?"
                        },
                    },
                    groups: {
                        create: "Gruppe anlegen",
                        table: {
                            name: group.de.name,
                            description: group.de.description,
                            users: "Mitglieder",
                            systemGroup: "Dies ist eine Systemgruppe und darf nicht gelöscht werden.",
                            actions: {
                                edit: "Gruppe bearbeiten",
                                editAria: "bearbeiten",
                                delete: "Gruppe entfernen",
                                deleteAria: "entfernen"
                            }
                        },
                        notification: {
                            success: "Die Gruppe '{{groupName}}' wurde erfolgreich gelöscht.",
                            error: "Die Gruppe '{{groupName}}' konnte nicht gelöscht werden."
                        },
                        confirmation: {
                            title: "Gruppe entfernen",
                            message: "Soll die Gruppe {{groupName}} entfernt werden?"
                        },
                    },
                    editGroup: {
                        labels: {
                            name: group.de.name,
                            description: group.de.description,
                        },
                        buttons: {
                            save: "Speichern",
                            back: "Zurück",
                            remove: "Entfernen",
                        },
                        notification: {
                            success: "Die Gruppe wurde erfolgreich gespeichert.",
                            error: "Die Gruppe konnte nicht gespeichert werden. Bitte versuchen Sie es später erneut.",
                        }
                    },
                    newGroup: {
                        labels: {
                            name: group.de.name,
                            description: group.de.description,
                        },
                        buttons: {
                            save: "Speichern"
                        },
                        errors: {
                            name: "Name muss ausgefüllt sein.",
                            nameLength: "Der Name muss eine Mindestlänge von 2 Zeichen haben.",
                        },
                        notification: {
                            success: "Die Gruppe wurde erfolgreich gespeichert.",
                            error: "Die Gruppe konnte nicht gespeichert werden. Bitte versuchen Sie es später erneut.",
                        },
                    },
                    editUser: {
                        labels: {
                            username: user.de.username,
                            givenName: user.de.givenName,
                            surname: user.de.surname,
                            displayName: user.de.displayName,
                            email: user.de.email,
                            password: user.de.password,
                            confirmPassword: user.de.confirmPassword
                        },
                        buttons: {
                            save: "Speichern"
                        },
                        alerts: {
                            success: "Die Account Informationen wurden erfolgreich gespeichert.",
                            error: "Die Account Informationen konnten nicht gespeichert werden. Bitte versuchen Sie es später erneut.",
                        },
                        errors: {
                            surname: "Nachname muss ausgefüllt sein.",
                            displayName: "Anzeigename muss ausgefüllt sein.",
                            email: {
                                invalid: "E-Mail ist ungültig.",
                                required: "E-Mail Addresse muss ausgefüllt sein.",
                            },
                            confirmPassword: "Password stimmt nicht überein.",
                            password: {
                                length: "Das Passwort muss mindestens {{length}} Zeichen lang sein.",
                                capital: "Das Passwort muss mindestens ein Großbuchstaben enthalten.",
                                lowercase: "Das Passwort muss mindestens ein Kleinbuchstaben enthalten.",
                                numeric: "Das Passwort muss mindestens eine Zahl enthalten.",
                                special: "Das Passwort muss mindestens ein Sonderzeichen enthalten.",
                            }
                        },
                    },
                    modal: {
                        confirm: "Bestätigen",
                        cancel: "Abbrechen"
                    }
                }
            },
        }
    });

export default i18n;
