import { useState } from 'react';

import { createFileRoute, useNavigate } from '@tanstack/react-router';

import axiosInstance, { login } from '@/api/api';

export const Route = createFileRoute('/login/')({
  component: RouteComponent,
});

function RouteComponent() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();
  const handleLogin = async () => {
    try {
      const response = await axiosInstance.post('/api/auth/signin', {
        email,
        password,
        platform: 'OURHOUR',
      });
      login(response.data.accessToken);
      navigate({ to: '/' });
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div>
      <div>로그인 페이지</div>
      <div>
        <input
          type="text"
          placeholder="아이디"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <input
          type="password"
          placeholder="비밀번호"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <button onClick={handleLogin}>로그인</button>
      </div>
    </div>
  );
}
