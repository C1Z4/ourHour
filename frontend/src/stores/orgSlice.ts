import { createSlice, PayloadAction } from '@reduxjs/toolkit';

import { MemberRoleKo } from '@/types/memberTypes';

interface OrgState {
  currentOrgId: number | null;
  currentRole: MemberRoleKo | null;
}

const getInitialOrgInfo = (): { orgId: number | null; role: MemberRoleKo | null } => {
  try {
    const orgInfoStr = sessionStorage.getItem('currentOrgInfo');
    if (orgInfoStr) {
      const orgInfo = JSON.parse(orgInfoStr);
      return { orgId: orgInfo.orgId || null, role: orgInfo.role || null };
    }
    return { orgId: null, role: null };
  } catch {
    return { orgId: null, role: null };
  }
};

const initialOrgInfo = getInitialOrgInfo();

const initialState: OrgState = {
  currentOrgId: initialOrgInfo.orgId,
  currentRole: initialOrgInfo.role,
};

const orgSlice = createSlice({
  name: 'activeOrgId',
  initialState,
  reducers: {
    setCurrentOrgInfo: (
      state,
      action: PayloadAction<{ orgId: number | null; role: MemberRoleKo | null }>,
    ) => {
      state.currentOrgId = action.payload.orgId;
      state.currentRole = action.payload.role;
      try {
        if (action.payload.orgId && action.payload.role) {
          sessionStorage.setItem(
            'currentOrgInfo',
            JSON.stringify({
              orgId: action.payload.orgId,
              role: action.payload.role,
            }),
          );
        } else {
          sessionStorage.removeItem('currentOrgInfo');
        }
      } catch (error) {
        console.warn('Failed to save org info to sessionStorage:', error);
      }
    },

    setCurrentOrgId: (state, action: PayloadAction<number | null>) => {
      state.currentOrgId = action.payload;
      try {
        if (action.payload && state.currentRole) {
          sessionStorage.setItem(
            'currentOrgInfo',
            JSON.stringify({
              orgId: action.payload,
              role: state.currentRole,
            }),
          );
        } else {
          sessionStorage.removeItem('currentOrgInfo');
        }
      } catch (error) {
        console.warn('Failed to save org info to sessionStorage:', error);
      }
    },

    clearCurrentOrgInfo: (state) => {
      state.currentOrgId = null;
      state.currentRole = null;
      try {
        sessionStorage.removeItem('currentOrgInfo');
      } catch (error) {
        console.warn('Failed to remove org info from sessionStorage:', error);
      }
    },
  },
});

export const { setCurrentOrgInfo, setCurrentOrgId, clearCurrentOrgInfo } = orgSlice.actions;

export default orgSlice.reducer;
