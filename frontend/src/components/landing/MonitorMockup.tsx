export function MonitorMockup() {
  return (
    <div
      className="flex-1 bg-gray-900 rounded-lg p-3 h-32"
      role="img"
      aria-label="모니터 화면 모킹업 - 코드 에디터 인터페이스"
    >
      <div className="space-y-1">
        <div className="flex gap-2" aria-hidden="true">
          <div className="w-3 h-3 bg-red-500 rounded-full" />
          <div className="w-3 h-3 bg-yellow-500 rounded-full" />
          <div className="w-3 h-3 bg-green-500 rounded-full" />
        </div>
        <div className="space-y-1 mt-2" aria-hidden="true">
          <div className="h-2 bg-green-400/60 rounded w-3/4" />
          <div className="h-2 bg-blue-400/60 rounded w-1/2" />
          <div className="h-2 bg-purple-400/60 rounded w-5/6" />
        </div>
      </div>
    </div>
  );
}
