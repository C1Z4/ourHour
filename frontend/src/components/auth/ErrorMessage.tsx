interface ErrorMessageProps {
  message: string;
  className?: string;
}

const ErrorMessage = ({ message, className = '' }: ErrorMessageProps) => {
  if (!message) {
    return null;
  }

  return (
    <div
      className={`bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg ${className}`}
    >
      {message}
    </div>
  );
};

export default ErrorMessage;
