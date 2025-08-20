import { Position } from '@/api/org/orgStructureApi';

import { PositionAccordion } from './PositionAccordion';

interface PositionsSectionProps {
  positions: Position[];
  orgId: number;
  expandedPositions: Set<number>;
  onTogglePosition: (positionId: number) => void;
}

export function PositionsSection({
  positions,
  orgId,
  expandedPositions,
  onTogglePosition,
}: PositionsSectionProps) {
  return (
    <div className="space-y-4">
      {positions?.map((position: Position) => (
        <PositionAccordion
          key={position.positionId}
          position={position}
          orgId={orgId}
          isExpanded={expandedPositions.has(position.positionId)}
          onToggle={() => onTogglePosition(position.positionId)}
        />
      ))}
    </div>
  );
}
