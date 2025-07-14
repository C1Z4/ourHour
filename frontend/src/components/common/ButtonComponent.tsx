import { Button, ButtonProps } from '@/components/ui/button';
import { cn } from '@/lib/utils';

interface ButtonComponentProps extends Omit<ButtonProps, 'variant' | 'size'> {
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost';
  size?: 'default' | 'sm' | 'lg' | 'icon';
}

export function ButtonComponent({
  variant = 'primary',
  size = 'default',
  className,
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
      {...props}
    />
  );
}
