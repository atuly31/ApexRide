import js from '@eslint/js';
import globals from 'globals';
import reactHooks from 'eslint-plugin-react-hooks';
import reactRefresh from 'eslint-plugin-react-refresh';
// import tseslint from 'typescript-eslint'; // REMOVED: No longer needed for TypeScript

export default [ // Changed from tseslint.config to a direct array export
  {
    ignores: ['dist'],
  },
  {
    // extends: [js.configs.recommended, ...tseslint.configs.recommended], // MODIFIED
    extends: [
      js.configs.recommended, // Standard ESLint recommended rules
    ],
    files: ['**/*.{js,jsx}'], // MODIFIED: Changed from ts,tsx to js,jsx
    languageOptions: {
      ecmaVersion: 2020,
      sourceType: 'module', // Added: Important for ES Modules
      globals: {
        ...globals.browser,
        // Add any other globals your project might use, e.g., if you use Jest, `jest: true`
      },
      parserOptions: { // Added: Necessary for JSX parsing
        ecmaFeatures: {
          jsx: true,
        },
      },
    },
    plugins: {
      'react-hooks': reactHooks,
      'react-refresh': reactRefresh,
    },
    rules: {
      // You might want to add more React-specific rules here
      // For example, if you want to enforce React in scope:
      // 'react/react-in-jsx-scope': 'off', // For React 17+ (new JSX transform)

      ...reactHooks.configs.recommended.rules,
      'react-refresh/only-export-components': [
        'warn',
        { allowConstantExport: true },
      ],
      // Often useful rules for React projects:
      // 'react/jsx-uses-react': 'off', // If using React 17+ new JSX transform
      // 'react/jsx-uses-vars': 'off', // If using React 17+ new JSX transform
    },
  },
];