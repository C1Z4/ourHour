import * as React from 'react';
import { cn } from '@/lib/utils';
import { BUTTON_COLORS } from '@/styles/colors';

interface ButtonComponentProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger';
  size?: 'small' | 'medium' | 'large';
  children: React.ReactNode;
  className?: string;
}

export function ButtonComponent({
  variant = 'primary',
  size = 'medium',
  children,
  className,
  ...props
}: ButtonComponentProps) {
  const baseClasses =
    'inline-flex items-center justify-center rounded-md font-medium transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed';

  const variantClasses = {
    primary: `bg-[${BUTTON_COLORS.primary.background}] text-white hover:bg-[${BUTTON_COLORS.primary.hover}] focus:ring-[${BUTTON_COLORS.primary.focus}]`,
    secondary: `bg-[${BUTTON_COLORS.secondary.background}] text-white hover:bg-[${BUTTON_COLORS.secondary.hover}] focus:ring-[${BUTTON_COLORS.secondary.focus}]`,
    danger: `bg-[${BUTTON_COLORS.danger.background}] text-white hover:bg-[${BUTTON_COLORS.danger.hover}] focus:ring-[${BUTTON_COLORS.danger.focus}]`,
  };

  const sizeClasses = {
    small: 'px-4 py-2 text-sm',
    medium: 'px-5 py-3 text-base',
    large: 'px-6 py-3 text-lg',
  };

  return (
    <button
      className={cn(baseClasses, variantClasses[variant], sizeClasses[size], className)}
      {...props}
    >
      {children}
    </button>
  );
}
