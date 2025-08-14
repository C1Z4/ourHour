const NotFoundPage = () => (
  <main className="flex items-center justify-center min-h-screen bg-background" role="main">
    <section className="text-center" aria-labelledby="not-found-title">
      <h1 id="not-found-title" className="text-4xl font-bold mb-4">
        404
      </h1>
      <p className="text-xl mb-8">페이지를 찾을 수 없습니다.</p>
      <a
        href="/frontend-old/public"
        className="px-4 py-2 bg-primary text-primary-foreground rounded-md"
        tabIndex={0}
        aria-label="홈으로 돌아가기"
        role="button"
      >
        홈으로 돌아가기
      </a>
    </section>
  </main>
);

export default NotFoundPage;
