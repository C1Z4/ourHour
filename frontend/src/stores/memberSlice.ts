import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface MemberState {
  memberNames: Record<number, string>;
}

const getInitialMemberNames = (): Record<number, string> => {
  try {
    const stored = sessionStorage.getItem('memberNames');
    return stored ? JSON.parse(stored) : {};
  } catch {
    return {};
  }
};

const initialState: MemberState = {
  memberNames: getInitialMemberNames(),
};

const memberSlice = createSlice({
  name: 'memberName',
  initialState,
  reducers: {
    setMemberName: (state, action: PayloadAction<{ orgId: number; memberName: string }>) => {
      const { orgId, memberName } = action.payload;
      state.memberNames[orgId] = memberName;
      try {
        sessionStorage.setItem('memberNames', JSON.stringify(state.memberNames));
      } catch (error) {
        console.warn('Failed to save member name to sessionStorage:', error);
      }
    },

    clearMemberName: (state, action: PayloadAction<number>) => {
      const orgId = action.payload;
      delete state.memberNames[orgId];
      try {
        sessionStorage.setItem('memberNames', JSON.stringify(state.memberNames));
      } catch (error) {
        console.warn('Failed to remove member name from sessionStorage:', error);
      }
    },

    clearAllMemberNames: (state) => {
      state.memberNames = {};
      try {
        sessionStorage.removeItem('memberNames');
      } catch (error) {
        console.warn('Failed to remove all member names from sessionStorage:', error);
      }
    },
  },
});

export const { setMemberName, clearMemberName, clearAllMemberNames } = memberSlice.actions;

export default memberSlice.reducer;
