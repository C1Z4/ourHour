import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface ProjectState {
  currentProjectName: string | null;
}

const getInitialProjectName = (): string | null => {
  try {
    return sessionStorage.getItem('currentProjectName');
  } catch {
    return null;
  }
};

const initialState: ProjectState = {
  currentProjectName: getInitialProjectName(),
};

const projectSlice = createSlice({
  name: 'projectName',
  initialState,
  reducers: {
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

    clearCurrentProjectName: (state) => {
      state.currentProjectName = null;
      try {
        sessionStorage.removeItem('currentProjectName');
      } catch (error) {
        console.warn('Failed to remove project name from sessionStorage:', error);
      }
    },
  },
});

export const { setCurrentProjectName, clearCurrentProjectName } = projectSlice.actions;

export default projectSlice.reducer;
