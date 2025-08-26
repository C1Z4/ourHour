import { Button, ButtonProps } from '@/components/ui/button';
import { cn } from '@/lib/utils';

interface ButtonComponentProps extends Omit<ButtonProps, 'variant' | 'size'> {
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost';
  size?: 'default' | 'sm' | 'lg' | 'icon';
  'aria-label'?: string;
  'aria-describedby'?: string;
  'aria-expanded'?: boolean;
  'aria-pressed'?: boolean;
  'aria-current'?: boolean;
}

export function ButtonComponent({
  variant = 'primary',
  size = 'default',
  className,
  'aria-label': ariaLabel,
  'aria-describedby': ariaDescribedby,
  'aria-expanded': ariaExpanded,
  'aria-pressed': ariaPressed,
  'aria-current': ariaCurrent,
  ...props
}: ButtonComponentProps) {
  const variantMap = {
    primary: 'customPrimary',
    secondary: 'customSecondary',
    danger: 'customDanger',
    ghost: 'ghost',
  } as const;

  return (
    <Button
      variant={variantMap[variant]}
      size={size}
      className={cn(className)}
      aria-label={ariaLabel}
      aria-describedby={ariaDescribedby}
      aria-expanded={ariaExpanded}
      aria-pressed={ariaPressed}
      aria-current={ariaCurrent}
      {...props}
    />
  );
}
