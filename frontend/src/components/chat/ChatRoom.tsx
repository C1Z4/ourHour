import { useState } from 'react';

import { useChat } from '@hooks/queries/chat/useChat';

import { ChatMessage } from '@/types/chatTypes';

export default function ChatTestPage() {
  const [roomId, setRoomId] = useState<number>(1);
  const [senderId, setSenderId] = useState<number>(1);
  const [message, setMessage] = useState<string>('');

  const { messages, sendMessage, isConnected } = useChat(roomId);

  const handleSendMessage = () => {
    if (message.trim() !== '') {
      sendMessage(message, senderId);
      setMessage('');
    }
  };

  return (
    <div style={{ border: '1px solid #ccc', padding: '20px' }}>
      <h1>채팅 테스트</h1>
      <p>연결 상태: {isConnected ? '✅ 연결됨' : '❌ 연결 끊김'}</p>

      <div style={{ marginBottom: '10px' }}>
        <label>채팅방 ID: </label>
        <input type="number" value={roomId} onChange={(e) => setRoomId(Number(e.target.value))} />
      </div>
      <div style={{ marginBottom: '10px' }}>
        <label>내 ID: </label>
        <input
          type="number"
          value={senderId}
          onChange={(e) => setSenderId(Number(e.target.value))}
        />
      </div>

      <hr style={{ margin: '20px 0' }} />

      <div
        style={{
          border: '1px solid #ccc',
          padding: '10px',
          height: '300px',
          overflowY: 'auto',
          marginBottom: '10px',
        }}
      >
        {messages.map((msg: ChatMessage, index: number) => (
          <div key={index}>
            <b>사용자 {msg.senderId}:</b> {msg.message}
          </div>
        ))}
      </div>

      <div>
        <input
          type="text"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === 'Enter') {
              handleSendMessage();
            }
          }}
          style={{ border: '1px solid #ccc', width: '600px', padding: '8px' }}
        />
        <button onClick={handleSendMessage} style={{ border: '1px solid #ccc', padding: '8px' }}>
          전송
        </button>
      </div>
    </div>
  );
}
