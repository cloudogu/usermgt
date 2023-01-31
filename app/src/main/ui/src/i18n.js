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
          editUser: {
            errors: {
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
        translation: {}
      },
    }
  });

export default i18n;
