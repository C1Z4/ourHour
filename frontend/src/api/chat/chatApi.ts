import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/chat-rooms';

export const getChatRoomList = async (memberId: number) => {
  const response = await axios.get(`${API_BASE_URL}`, {
    params: {
      memberId: memberId.toString(),
    },
  });
  return response.data;
};

export const getChatMessages = async (roomId: number) => {
  const response = await axios.get(`${API_BASE_URL}/${roomId}/messages`);
  return response.data;
};
