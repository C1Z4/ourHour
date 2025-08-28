interface FeatureCardProps {
  title: string;
  description: string;
  features: string[];
  icon: React.ReactNode;
  gradientFrom: string;
  gradientTo: string;
  dotColor: string;
}

export function FeatureCard({
  title,
  description,
  features,
  icon,
  gradientFrom,
  gradientTo,
  dotColor,
}: FeatureCardProps) {
  return (
    <article className="group relative" role="listitem" aria-labelledby={`feature-title-${title}`}>
      <div
        className={`absolute inset-0 bg-gradient-to-r from-[${gradientFrom}]/10 to-[${gradientTo}]/10 rounded-3xl transform group-hover:scale-105 transition-transform duration-300`}
      />
      <div className="relative bg-white/80 backdrop-blur-sm rounded-3xl p-8 border border-white/20 shadow-xl">
        <div className="flex items-start space-x-6">
          <div className="flex-shrink-0">
            <div
              className={`w-16 h-16 bg-gradient-to-br from-[${gradientFrom}] to-[${gradientTo}] rounded-2xl flex items-center justify-center shadow-lg`}
              aria-hidden="true"
            >
              {icon}
            </div>
          </div>
          <div className="flex-1">
            <h3 id={`feature-title-${title}`} className="text-2xl font-bold text-gray-900 mb-3">
              {title}
            </h3>
            <p className="text-gray-600 leading-relaxed mb-4">{description}</p>
            <ul
              className="space-y-2 text-sm text-gray-500"
              role="list"
              aria-label={`${title} 기능 목록`}
            >
              {features.map((feature, index) => (
                <li key={index} className="flex items-center" role="listitem">
                  <span
                    className={`w-1.5 h-1.5 bg-[${dotColor}] rounded-full mr-2`}
                    aria-hidden="true"
                  />
                  {feature}
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>
    </article>
  );
}
