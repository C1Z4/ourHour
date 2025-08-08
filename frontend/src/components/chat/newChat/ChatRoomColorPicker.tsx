import { Label } from '@/components/ui/label.tsx';
import { CHAT_COLORS } from '@/styles/colors.ts';

interface Props {
  selectedColor: string;
  onChangeColor: (color: keyof typeof CHAT_COLORS) => void;
}
export const ChatRoomColorPicker = ({ selectedColor, onChangeColor }: Props) => (
  <div className="grid gap-3">
    <Label htmlFor="color">태그 색상</Label>
    <div className="flex justify-center gap-8">
      {(Object.keys(CHAT_COLORS) as Array<keyof typeof CHAT_COLORS>).map((name) => (
        <label key={name} style={{ cursor: 'pointer', position: 'relative' }}>
          <input
            type="radio"
            name="color"
            value={name}
            style={{ display: 'none' }}
            checked={selectedColor === name}
            onChange={() => onChangeColor(name)}
          />
          <span
            className={`inline-block w-11 h-11 rounded-full border-2 transition-all ${
              selectedColor === name ? 'border-blue-500 scale-110' : 'border-transparent'
            }`}
            style={{ backgroundColor: CHAT_COLORS[name] }}
          />
        </label>
      ))}
    </div>
  </div>
);
