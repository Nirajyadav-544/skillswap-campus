import { useState, type FormEvent } from "react";

interface SkillSearchProps {
  initialValue?: string;
  onSearch: (value: string) => void;
  placeholder?: string;
}

const SkillSearch = ({
  initialValue = "",
  onSearch,
  placeholder = "Search skills...",
}: SkillSearchProps) => {
  const [value, setValue] = useState(initialValue);

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    onSearch(value.trim());
  };

  const handleClear = () => {
    setValue("");
    onSearch("");
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="w-full"
    >
      <div
        className="
          flex flex-col gap-3 rounded-2xl
          border border-slate-200 bg-white p-3
          shadow-sm sm:flex-row
        "
      >
        <div className="relative flex-1">
          <span
            className="
              pointer-events-none absolute left-4 top-1/2
              -translate-y-1/2 text-slate-400
            "
            aria-hidden="true"
          >
            ⌕
          </span>

          <input
            type="search"
            value={value}
            onChange={(event) => setValue(event.target.value)}
            placeholder={placeholder}
            className="
              h-11 w-full rounded-xl border border-slate-200
              bg-slate-50 pl-11 pr-10 text-sm text-slate-900
              placeholder:text-slate-400
              outline-none transition
              focus:border-indigo-400 focus:bg-white
              focus:ring-2 focus:ring-indigo-100
            "
          />

          {value && (
            <button
              type="button"
              onClick={handleClear}
              className="
                absolute right-3 top-1/2
                -translate-y-1/2 rounded-lg
                px-2 py-1 text-sm text-slate-400
                hover:bg-slate-100 hover:text-slate-700
              "
              aria-label="Clear search"
            >
              ×
            </button>
          )}
        </div>

        <button
          type="submit"
          className="
            h-11 rounded-xl bg-indigo-600 px-6
            text-sm font-semibold text-white
            shadow-sm transition
            hover:bg-indigo-700
            focus:outline-none focus:ring-2
            focus:ring-indigo-500 focus:ring-offset-2
          "
        >
          Search
        </button>
      </div>
    </form>
  );
};

export default SkillSearch;