import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface ProjectState {
  currentProjectId: string | null;
  currentProjectName: string | null;
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

const initialState: ProjectState = {
  currentProjectId: getInitialProjectId(),
  currentProjectName: getInitialProjectName(),
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

export const { setCurrentProjectId, setCurrentProjectName, clearCurrentProject } =
  projectSlice.actions;

export default projectSlice.reducer;
