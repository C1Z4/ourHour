import { ParticipantSummary } from '@/api/project/projectApi';

interface ParticipantsListProps {
  participants: ParticipantSummary[];
  maxVisible?: number;
}

export function ParticipantsList({ participants, maxVisible = 3 }: ParticipantsListProps) {
  const visibleParticipants = participants.slice(0, maxVisible);
  const isOverflow = participants.length > maxVisible;

  return (
    <div className="flex flex-wrap gap-1">
      {visibleParticipants.map((participant, index) => (
        <span
          key={index}
          className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800"
        >
          {participant.memberName}
        </span>
      ))}
      {isOverflow && (
        <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-gray-100 text-gray-600">
          ...
        </span>
      )}
    </div>
  );
}
