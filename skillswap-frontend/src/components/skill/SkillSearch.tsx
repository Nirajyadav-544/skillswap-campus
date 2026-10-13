import React, {
  useEffect,
  useRef,
  useState,
} from "react";

import type { SkillSummary } from "../../types/skill/skill.types";

import useSkillSearch from "../../hooks/skill/useSkillSearch";

interface SkillSearchProps {
  value?: string;
  onChange?: (value: string) => void;
  onSelect?: (skill: SkillSummary) => void;
  onResults?: (results: SkillSummary[]) => void;
  placeholder?: string;
  disabled?: boolean;
  autoFocus?: boolean;
  minCharacters?: number;
  debounceMs?: number;
  maxResults?: number;
  label?: string;
}

export function SkillSearch({
  value,
  onChange,
  onSelect,
  onResults,
  placeholder = "Search skills...",
  disabled = false,
  autoFocus = false,
  minCharacters = 1,
  debounceMs = 300,
  maxResults = 10,
  label = "Search skills",
}: SkillSearchProps) {
  const [internalValue, setInternalValue] =
    useState(value ?? "");

  const containerRef =
    useRef<HTMLDivElement>(null);

  const {
    results,
    loading,
    error,
    searchWithDebounce,
    clearSearch,
  } = useSkillSearch({
    debounceMs,
  });

  const query =
    value !== undefined
      ? value
      : internalValue;

  useEffect(() => {
    if (value !== undefined) {
      setInternalValue(value);
    }
  }, [value]);

  useEffect(() => {
    onResults?.(results);
  }, [results, onResults]);

  useEffect(() => {
    const normalizedQuery = query.trim();

    if (
      disabled ||
      normalizedQuery.length < minCharacters
    ) {
      clearSearch();
      return;
    }

    void searchWithDebounce(normalizedQuery);
  }, [
    query,
    disabled,
    minCharacters,
    searchWithDebounce,
    clearSearch,
  ]);

  useEffect(() => {
    const handleOutsideClick = (
      event: MouseEvent,
    ) => {
      if (
        containerRef.current &&
        !containerRef.current.contains(
          event.target as Node,
        )
      ) {
        clearSearch();
      }
    };

    document.addEventListener(
      "mousedown",
      handleOutsideClick,
    );

    return () => {
      document.removeEventListener(
        "mousedown",
        handleOutsideClick,
      );
    };
  }, [clearSearch]);

  const handleChange = (
    event: React.ChangeEvent<HTMLInputElement>,
  ) => {
    const nextValue = event.target.value;

    setInternalValue(nextValue);
    onChange?.(nextValue);
  };

  const handleSelect = (
    skill: SkillSummary,
  ) => {
    setInternalValue(skill.name);
    onChange?.(skill.name);
    onSelect?.(skill);
    clearSearch();
  };

  const visibleResults =
    results.slice(0, maxResults);

  const showDropdown =
    !disabled &&
    query.trim().length >= minCharacters &&
    (loading ||
      error !== null ||
      visibleResults.length > 0);

  return (
    <div
      ref={containerRef}
      className="relative w-full"
    >
      <label className="mb-1.5 block text-sm font-medium text-slate-700">
        {label}
      </label>

      <div className="relative">
        <input
          type="text"
          value={query}
          onChange={handleChange}
          placeholder={placeholder}
          disabled={disabled}
          autoFocus={autoFocus}
          autoComplete="off"
          className={[
            "w-full rounded-xl border bg-white px-4 py-2.5 pr-10 text-sm text-slate-900 outline-none transition",
            "placeholder:text-slate-400",
            "focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100",
            disabled
              ? "cursor-not-allowed bg-slate-100 opacity-70"
              : "border-slate-300",
          ].join(" ")}
        />

        {loading && (
          <span
            className="absolute right-3 top-1/2 h-4 w-4 -translate-y-1/2 animate-spin rounded-full border-2 border-slate-300 border-t-indigo-600"
            aria-label="Searching"
          />
        )}
      </div>

      {showDropdown && (
        <div className="absolute z-30 mt-2 w-full overflow-hidden rounded-xl border border-slate-200 bg-white shadow-lg">
          {loading && (
            <div className="px-4 py-3 text-sm text-slate-500">
              Searching skills...
            </div>
          )}

          {!loading && error && (
            <div className="px-4 py-3 text-sm text-red-600">
              {error}
            </div>
          )}

          {!loading &&
            !error &&
            visibleResults.length > 0 && (
              <div
                role="listbox"
                aria-label="Skill search results"
                className="max-h-80 overflow-y-auto py-1"
              >
                {visibleResults.map(
                  (skill) => (
                    <button
                      key={skill.id}
                      type="button"
                      role="option"
                      onClick={() =>
                        handleSelect(skill)
                      }
                      className="w-full px-4 py-3 text-left transition hover:bg-slate-50"
                    >
                      <div className="flex items-center justify-between gap-3">
                        <div className="min-w-0">
                          <p className="truncate text-sm font-medium text-slate-900">
                            {skill.name}
                          </p>

                          <p className="mt-0.5 text-xs text-slate-500">
                            {skill.category
                              .replace(
                                /_/g,
                                " ",
                              )
                              .toLowerCase()
                              .replace(
                                /\b\w/g,
                                (letter) =>
                                  letter.toUpperCase(),
                              )}
                          </p>
                        </div>

                        <span className="shrink-0 text-xs text-slate-400">
                          {skill.userCount} users
                        </span>
                      </div>
                    </button>
                  ),
                )}
              </div>
            )}

          {!loading &&
            !error &&
            visibleResults.length === 0 && (
              <div className="px-4 py-3 text-sm text-slate-500">
                No matching skills found.
              </div>
            )}
        </div>
      )}
    </div>
  );
}

export default SkillSearch;