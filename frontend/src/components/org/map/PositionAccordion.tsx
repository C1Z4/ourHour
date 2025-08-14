import { Member } from '@/types/memberTypes';

import { Position } from '@/api/org/orgStructureApi';
import { AccordionSection } from '@/components/org/map/AccordionSection';
import { usePositionMembersQuery } from '@/hooks/queries/org/useOrgStructureQueries';

interface PositionAccordionProps {
  position: Position;
  orgId: number;
  isExpanded: boolean;
  onToggle: () => void;
}

export function PositionAccordion({
  position,
  orgId,
  isExpanded,
  onToggle,
}: PositionAccordionProps) {
  const { data: positionMembersResponse, isLoading } = usePositionMembersQuery(
    orgId,
    position.positionId,
    isExpanded,
  );
  const positionMembers = positionMembersResponse as unknown as Member[];

  return (
    <AccordionSection
      title={position.name}
      memberCount={position.memberCount}
      isExpanded={isExpanded}
      onToggle={onToggle}
      members={positionMembers}
      isLoading={isLoading}
    />
  );
}
