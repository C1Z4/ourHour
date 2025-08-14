import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface OrgState {
  currentOrgId: number | null;
}

const getInitialOrgId = (): number | null => {
  try {
    return sessionStorage.getItem('currentOrgId')
      ? Number(sessionStorage.getItem('currentOrgId'))
      : null;
  } catch {
    return null;
  }
};

const initialState: OrgState = {
  currentOrgId: getInitialOrgId(),
};

const orgSlice = createSlice({
  name: 'activeOrgId',
  initialState,
  reducers: {
    setCurrentOrgId: (state, action: PayloadAction<number | null>) => {
      state.currentOrgId = action.payload;
      try {
        if (action.payload) {
          sessionStorage.setItem('currentOrgId', action.payload.toString());
        } else {
          sessionStorage.removeItem('currentOrgId');
        }
      } catch (error) {
        console.warn('Failed to save org id to sessionStorage:', error);
      }
    },

    clearCurrentOrgId: (state) => {
      state.currentOrgId = null;
      try {
        sessionStorage.removeItem('currentOrgId');
      } catch (error) {
        console.warn('Failed to remove org id from sessionStorage:', error);
      }
    },
  },
});

export const { setCurrentOrgId, clearCurrentOrgId } = orgSlice.actions;

export default orgSlice.reducer;
