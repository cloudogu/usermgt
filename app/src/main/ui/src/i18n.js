import i18n from 'i18next';
import {initReactI18next} from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';

i18n
  // detect user language
  // learn more: https://github.com/i18next/i18next-browser-languageDetector
  .use(LanguageDetector)
  // pass the i18n instance to react-i18next.
  .use(initReactI18next)
  // init i18next
  // for all options read: https://www.i18next.com/overview/configuration-options
  .init({
    debug: true, fallbackLng: 'en', interpolation: {
      escapeValue: false, // not needed for react as it escapes by default
    }, resources: {
      en: {
        translation: {
          pages: {
            account: 'Account',
            users: 'Users',
            groups: 'Groups',
          },
          navbar: {
            logout: 'Logout',
          },
          editUser: {
            labels: {
              username: 'Username',
              givenName: 'Given Name',
              surname: 'Surname',
              displayName: 'Display Name',
              email: 'E-Mail',
              password: 'Password',
              confirmPassword: 'Confirm Password'

            },
            buttons: {
              save: 'Save'
            },
            alerts: {
              success: 'Account information saved successfully.',
              error: 'Account information could not be saved. Please try again later.',
            },
            errors: {
              surname: 'Surname is required.',
              displayName: 'Display name is required.',
              email: {
                invalid: 'E-mail address is invalid.',
                required: '\'E-mail address is required.\'',
              },
              confirmPassword: 'Passwords must match.',
              password: {
                length: 'The password must contain at least {{length}} characters.',
                capital: 'The password must contain at least one capital letter.',
                lowercase: 'The password must contain at least one lower case letter.',
                numeric: 'The password must contain at least 1 number.',
                special: 'The password must contain at least 1 special character.',
              }
            },
          }
        }
      }, de: {
        translation: {
          pages: {
            account: 'Account',
            users: 'Nutzer',
            groups: 'Gruppen',
          },
          navbar: {
            logout: 'Abmelden',
          },
          editUser: {
            labels: {
              username: 'Nutzername',
              givenName: 'Vorname',
              surname: 'Nachname',
              displayName: 'Anzeigename',
              email: 'E-Mail',
              password: 'Passwort',
              confirmPassword: 'Passwort bestätigen'

            },
            buttons: {
              save: 'Speichern'
            },
            alerts: {
              success: 'Die Account Informationen wurden erfolgreich gespeichert.',
              error: 'Die Account Informationen konnten nicht gespeichert werden. Bitte versuchen Sie es später erneut.',
            },
            errors: {
              surname: 'Nachname muss ausgefüllt sein.',
              displayName: 'Anzeigename muss ausgefüllt sein.',
              email: {
                invalid: 'E-Mail ist nicht valide',
                required: 'E-Mail Addresse muss ausgefüllt sein.',
              },
              confirmPassword: 'Password stimmt nicht überein.',
              password: {
                length: 'Das Passwort muss mindestens {{length}} Zeichen lang sein.',
                capital: 'Das Passwort muss mindestens ein Großbuchstaben enthalten.',
                lowercase: 'Das Passwort muss mindestens ein Kleinbuchstaben enthalten.',
                numeric: 'Das Passwort muss mindestens eine Zahl enthalten.',
                special: 'Das Passwort muss mindestens ein Sonderzeichen enthalten.',
              }
            },
          }
        }
      },
    }
  });

export default i18n;
