import js from '@eslint/js';
import importPlugin from 'eslint-plugin-import';
import prettierPlugin from 'eslint-plugin-prettier';
import reactPlugin from 'eslint-plugin-react';
import reactHooks from 'eslint-plugin-react-hooks';
import reactRefresh from 'eslint-plugin-react-refresh';
import globals from 'globals';
import tseslint from 'typescript-eslint';

export default tseslint.config([
  {
    ignores: ['dist/**', 'build/**', 'node_modules/**'],
  },
  {
    files: ['**/*.{ts,tsx,js,jsx}'],
    extends: [js.configs.recommended, ...tseslint.configs.recommended],
    languageOptions: {
      ecmaVersion: 2020,
      globals: {
        ...globals.browser,
        ...globals.es2020,
      },
      parserOptions: {
        ecmaFeatures: {
          jsx: true,
        },
      },
    },
    plugins: {
      react: reactPlugin,
      'react-hooks': reactHooks,
      'react-refresh': reactRefresh,
      import: importPlugin,
      prettier: prettierPlugin,
    },
    settings: {
      react: {
        version: 'detect',
      },
    },
    rules: {
      'prettier/prettier': 'error', // prettier 연동

      'react/jsx-uses-react': 'off', // React 17+ 자동 JSX 변환으로 불필요
      'react/react-in-jsx-scope': 'off', // React 17+ 자동 JSX 변환으로 불필요
      'react/jsx-no-duplicate-props': 'error', // JSX에서 중복 props 금지
      'react/jsx-no-undef': 'error', // 정의되지 않은 JSX 컴포넌트 사용 금지
      'react/jsx-pascal-case': 'error', // JSX 컴포넌트는 PascalCase 사용
      'react/no-danger-with-children': 'error', // dangerouslySetInnerHTML과 children 동시 사용 금지
      'react/no-deprecated': 'error', // 사용 중단된 React API 사용 금지
      'react/no-direct-mutation-state': 'error', // state 직접 변경 금지
      'react/no-find-dom-node': 'error', // ReactDOM.findDOMNode 사용 금지
      'react/no-is-mounted': 'error', // isMounted 사용 금지
      'react/no-render-return-value': 'error', // ReactDOM.render 반환값 사용 금지
      'react/require-render-return': 'error', // render 메서드는 반드시 return문 필요
      'react/self-closing-comp': 'error', // 자식이 없는 컴포넌트는 self-closing 태그 사용
      'react/no-unescaped-entities': 'error', // JSX에서 이스케이프되지 않은 엔티티 금지

      '@typescript-eslint/no-unused-vars': ['warn', { argsIgnorePattern: '^_' }], // 사용하지 않는 변수 경고
      'no-console': 'warn', // console.log 사용 경고
      'no-debugger': 'error', // debugger 문 사용 금지
      'no-alert': 'warn', // alert, confirm, prompt 사용 경고
      'no-var': 'error', // var 사용 금지 (let, const 사용)
      'prefer-const': 'error', // 재할당하지 않는 변수는 const 사용
      'no-unused-expressions': 'error', // 사용되지 않는 표현식 금지
      'no-duplicate-imports': 'error', // 중복 import 금지
      'no-useless-return': 'error', // 불필요한 return문 금지
      'no-useless-concat': 'error', // 불필요한 문자열 연결 금지
      'no-lonely-if': 'error', // else 블록 안의 단독 if문 금지
      'no-else-return': 'error', // if-return 패턴에서 불필요한 else 금지
      'no-nested-ternary': 'error', // 중첩된 삼항 연산자 금지
      'no-unneeded-ternary': 'error', // 불필요한 삼항 연산자 금지
      'prefer-template': 'error', // 문자열 연결보다 템플릿 리터럴 선호
      'prefer-arrow-callback': 'error', // 콜백에서 화살표 함수 선호
      'arrow-body-style': ['error', 'as-needed'], // 화살표 함수에서 불필요한 중괄호 제거

      eqeqeq: ['error', 'always'], // === 및 !== 사용 강제
      curly: 'error', // 모든 제어문에 중괄호 사용
      'consistent-return': 'error', // 함수에서 일관된 return 패턴 사용

      camelcase: ['error', { properties: 'never' }], // camelCase 사용 (객체 속성 제외)

      quotes: ['error', 'single', { avoidEscape: true }], // JavaScript에서 single quote 사용
      'jsx-quotes': ['error', 'prefer-double'], // JSX 속성에서 double quote 사용

      // Import 순서 및 그룹화 규칙
      'import/order': [
        'error',
        {
          groups: [
            'builtin', // Node.js 내장 모듈
            'external', // 외부 라이브러리
            'internal', // 내부 모듈 (절대 경로)
            ['parent', 'sibling'], // 상대 경로
            'index', // index 파일
          ],
          pathGroups: [
            // React 생태계 우선 (external 그룹의 맨 앞)
            { pattern: 'react', group: 'external', position: 'before' },
            { pattern: 'react-dom', group: 'external', position: 'before' },
            { pattern: 'react-router-dom', group: 'external', position: 'before' },
            { pattern: 'react-router', group: 'external', position: 'before' },
            { pattern: '@tanstack/react-query', group: 'external', position: 'before' },
            { pattern: '@reduxjs/toolkit', group: 'external', position: 'before' },
            { pattern: 'react-redux', group: 'external', position: 'before' },

            { pattern: '@api/**', group: 'internal', position: 'before' },
            { pattern: '@/types/**', group: 'internal', position: 'before' },
            { pattern: '@stores/**', group: 'internal', position: 'before' },
            { pattern: '@hooks/**', group: 'internal', position: 'before' },
            { pattern: '@components/**', group: 'internal', position: 'before' },
            { pattern: '@pages/**', group: 'internal', position: 'before' },
            { pattern: '@router/**', group: 'internal', position: 'before' },

            { pattern: '@lib/**', group: 'internal', position: 'after' },
            { pattern: '@utils/**', group: 'internal', position: 'after' },
            { pattern: '@constants/**', group: 'internal', position: 'after' },
            { pattern: '@styles/**', group: 'internal', position: 'after' },
            { pattern: '@assets/**', group: 'internal', position: 'after' },

            { pattern: '@/**', group: 'internal', position: 'after' },
          ],
          pathGroupsExcludedImportTypes: ['react', 'builtin'],
          'newlines-between': 'always', // 그룹 간 빈 줄 강제
          alphabetize: {
            order: 'asc', // 알파벳 순서로 정렬
            caseInsensitive: true,
          },
        },
      ],
      'import/no-duplicates': 'error', // 중복 import 방지

      // React Hooks 규칙
      ...reactHooks.configs.recommended.rules,

      // React Refresh 규칙
      'react-refresh/only-export-components': ['warn', { allowConstantExport: true }],
    },
  },
]);
