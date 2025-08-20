import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface ProjectState {
  currentProjectId: string | null;
  currentProjectName: string | null;
  isMyIssuesOnly: boolean;
}

const getInitialProjectId = (): string | null => {
  try {
    return sessionStorage.getItem('currentProjectId');
  } catch {
    return null;
  }
};

const getInitialProjectName = (): string | null => {
  try {
    return sessionStorage.getItem('currentProjectName');
  } catch {
    return null;
  }
};

const getInitialIsMyIssuesOnly = (): boolean => {
  try {
    const stored = localStorage.getItem('isMyIssuesOnly');
    return stored ? JSON.parse(stored) : false;
  } catch {
    return false;
  }
};

const initialState: ProjectState = {
  currentProjectId: getInitialProjectId(),
  currentProjectName: getInitialProjectName(),
  isMyIssuesOnly: getInitialIsMyIssuesOnly(),
};

const projectSlice = createSlice({
  name: 'projectName',
  initialState,
  reducers: {
    setCurrentProjectId: (state, action: PayloadAction<string | null>) => {
      state.currentProjectId = action.payload;
      try {
        if (action.payload) {
          sessionStorage.setItem('currentProjectId', action.payload);
        } else {
          sessionStorage.removeItem('currentProjectId');
        }
      } catch (error) {
        console.warn('Failed to save project id to sessionStorage:', error);
      }
    },

    setCurrentProjectName: (state, action: PayloadAction<string | null>) => {
      state.currentProjectName = action.payload;
      try {
        if (action.payload) {
          sessionStorage.setItem('currentProjectName', action.payload);
        } else {
          sessionStorage.removeItem('currentProjectName');
        }
      } catch (error) {
        console.warn('Failed to save project name to sessionStorage:', error);
      }
    },

    setIsMyIssuesOnly: (state, action: PayloadAction<boolean>) => {
      state.isMyIssuesOnly = action.payload;
      try {
        localStorage.setItem('isMyIssuesOnly', JSON.stringify(action.payload));
      } catch (error) {
        console.warn('Failed to save isMyIssuesOnly to localStorage:', error);
      }
    },

    toggleIsMyIssuesOnly: (state) => {
      state.isMyIssuesOnly = !state.isMyIssuesOnly;
      try {
        localStorage.setItem('isMyIssuesOnly', JSON.stringify(state.isMyIssuesOnly));
      } catch (error) {
        console.warn('Failed to save isMyIssuesOnly to localStorage:', error);
      }
    },

    clearCurrentProject: (state) => {
      state.currentProjectName = null;
      try {
        sessionStorage.removeItem('currentProjectName');
        sessionStorage.removeItem('currentProjectId');
      } catch (error) {
        console.warn('Failed to remove project name and id from sessionStorage:', error);
      }
    },
  },
});

export const {
  setCurrentProjectId,
  setCurrentProjectName,
  setIsMyIssuesOnly,
  toggleIsMyIssuesOnly,
  clearCurrentProject,
} = projectSlice.actions;

export default projectSlice.reducer;
