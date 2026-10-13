import type { ChangeEvent } from "react";

interface SkillCategoryFilterProps {
  categories: string[];
  value?: string;
  onChange: (category: string) => void;
  label?: string;
}

const SkillCategoryFilter = ({
  categories,
  value = "",
  onChange,
  label = "Category",
}: SkillCategoryFilterProps) => {
  const handleChange = (event: ChangeEvent<HTMLSelectElement>) => {
    onChange(event.target.value);
  };

  return (
    <div className="w-full sm:w-auto">
      <label
        htmlFor="skill-category-filter"
        className="mb-2 block text-sm font-semibold text-slate-700"
      >
        {label}
      </label>

      <div className="relative">
        <select
          id="skill-category-filter"
          value={value}
          onChange={handleChange}
          className="
            h-11 w-full min-w-48 appearance-none rounded-xl
            border border-slate-200 bg-white px-4 pr-10
            text-sm font-medium text-slate-700
            shadow-sm outline-none transition
            hover:border-slate-300
            focus:border-indigo-400
            focus:ring-2 focus:ring-indigo-100
          "
        >
          <option value="">All categories</option>

          {categories.map((category) => (
            <option key={category} value={category}>
              {category}
            </option>
          ))}
        </select>

        <span
          className="
            pointer-events-none absolute right-4 top-1/2
            -translate-y-1/2 text-slate-400
          "
          aria-hidden="true"
        >
          ▾
        </span>
      </div>
    </div>
  );
};

export default SkillCategoryFilter;