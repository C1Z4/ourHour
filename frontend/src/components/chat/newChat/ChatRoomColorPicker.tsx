import { Label } from '@/components/ui/label.tsx';
import { CHAT_COLORS } from '@/styles/colors.ts';

export const ChatRoomColorPicker = () => (
  <div className="grid gap-3">
    <Label htmlFor="color">태그 색상</Label>
    <div style={{ display: 'flex', gap: '1rem' }}>
      {Object.entries(CHAT_COLORS).map(([name, hex]) => (
        <label key={name} style={{ cursor: 'pointer', position: 'relative' }}>
          <input type="radio" name="color" value={name} style={{ display: 'none' }} />
          <span
            style={{
              display: 'inline-block',
              width: '45px',
              height: '45px',
              borderRadius: '50%',
              backgroundColor: hex,
              border: `2px solid ${hex}`,
              boxShadow: '0 0 4px rgba(0, 0, 0, 0.1)',
            }}
          />
        </label>
      ))}
    </div>
  </div>
);
