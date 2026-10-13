import { useEffect, useState } from "react";

import type {
  Profile,
  UpdateProfileRequest,
} from "../../types/profile/profile.types";

interface ProfileEditFormProps {
  profile: Profile | null;
  loading?: boolean;
  onSubmit: (data: UpdateProfileRequest) => Promise<void>;
  onCancel: () => void;
}

interface FormState {
  headline: string;
  bio: string;
  college: string;
  department: string;
  course: string;
  yearOfStudy: string;
  graduationYear: string;
  location: string;
  profileImageUrl: string;
  githubUrl: string;
  linkedinUrl: string;
  portfolioUrl: string;
  availability: string;
}

const emptyForm: FormState = {
  headline: "",
  bio: "",
  college: "",
  department: "",
  course: "",
  yearOfStudy: "",
  graduationYear: "",
  location: "",
  profileImageUrl: "",
  githubUrl: "",
  linkedinUrl: "",
  portfolioUrl: "",
  availability: "",
};

const ProfileEditForm = ({
  profile,
  loading = false,
  onSubmit,
  onCancel,
}: ProfileEditFormProps) => {
  const [form, setForm] = useState<FormState>(emptyForm);
  const [validationError, setValidationError] = useState<string | null>(null);

  useEffect(() => {
    if (!profile) {
      setForm(emptyForm);
      return;
    }

    setForm({
      headline: profile.headline ?? "",
      bio: profile.bio ?? "",
      college: profile.college ?? "",
      department: profile.department ?? "",
      course: profile.course ?? "",
      yearOfStudy: profile.yearOfStudy?.toString() ?? "",
      graduationYear: profile.graduationYear?.toString() ?? "",
      location: profile.location ?? "",
      profileImageUrl: profile.profileImageUrl ?? "",
      githubUrl: profile.githubUrl ?? "",
      linkedinUrl: profile.linkedinUrl ?? "",
      portfolioUrl: profile.portfolioUrl ?? "",
      availability: profile.availability ?? "",
    });
  }, [profile]);

  const updateField = (
    field: keyof FormState,
    value: string,
  ) => {
    setForm((current) => ({
      ...current,
      [field]: value,
    }));

    if (validationError) {
      setValidationError(null);
    }
  };

  const handleSubmit = async (
    event: React.FormEvent<HTMLFormElement>,
  ) => {
    event.preventDefault();

    if (form.yearOfStudy) {
      const year = Number(form.yearOfStudy);

      if (!Number.isInteger(year) || year < 1 || year > 10) {
        setValidationError(
          "Year of study must be between 1 and 10.",
        );
        return;
      }
    }

    if (form.graduationYear) {
      const year = Number(form.graduationYear);

      if (
        !Number.isInteger(year) ||
        year < 2000 ||
        year > 2100
      ) {
        setValidationError(
          "Graduation year must be between 2000 and 2100.",
        );
        return;
      }
    }

    const data: UpdateProfileRequest = {
      headline: form.headline.trim() || undefined,
      bio: form.bio.trim() || undefined,
      college: form.college.trim() || undefined,
      department: form.department.trim() || undefined,
      course: form.course.trim() || undefined,
      yearOfStudy: form.yearOfStudy
        ? Number(form.yearOfStudy)
        : undefined,
      graduationYear: form.graduationYear
        ? Number(form.graduationYear)
        : undefined,
      location: form.location.trim() || undefined,
      profileImageUrl:
        form.profileImageUrl.trim() || undefined,
      githubUrl: form.githubUrl.trim() || undefined,
      linkedinUrl:
        form.linkedinUrl.trim() || undefined,
      portfolioUrl:
        form.portfolioUrl.trim() || undefined,
      availability:
        form.availability.trim() || undefined,
    };

    await onSubmit(data);
  };

  const inputClass =
    "mt-2 w-full rounded-xl border border-slate-200 bg-white px-3.5 py-2.5 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100 disabled:bg-slate-50";

  const labelClass =
    "text-sm font-semibold text-slate-700";

  return (
    <form
      onSubmit={handleSubmit}
      className="space-y-7"
    >
      {validationError && (
        <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3">
          <p className="text-sm font-medium text-red-700">
            {validationError}
          </p>
        </div>
      )}

      <section>
        <h2 className="text-lg font-bold text-slate-900">
          About You
        </h2>

        <p className="mt-1 text-sm text-slate-500">
          Tell other students what you are interested in.
        </p>

        <div className="mt-5 space-y-5">
          <div>
            <label htmlFor="headline" className={labelClass}>
              Headline
            </label>

            <input
              id="headline"
              value={form.headline}
              onChange={(event) =>
                updateField("headline", event.target.value)
              }
              maxLength={200}
              disabled={loading}
              placeholder="e.g. Full Stack Developer | AI Enthusiast"
              className={inputClass}
            />
          </div>

          <div>
            <label htmlFor="bio" className={labelClass}>
              Bio
            </label>

            <textarea
              id="bio"
              value={form.bio}
              onChange={(event) =>
                updateField("bio", event.target.value)
              }
              maxLength={2000}
              rows={5}
              disabled={loading}
              placeholder="Tell people about yourself, your interests and goals..."
              className={`${inputClass} resize-y`}
            />

            <p className="mt-1 text-right text-xs text-slate-400">
              {form.bio.length}/2000
            </p>
          </div>
        </div>
      </section>

      <section className="border-t border-slate-100 pt-7">
        <h2 className="text-lg font-bold text-slate-900">
          Education
        </h2>

        <div className="mt-5 grid gap-5 sm:grid-cols-2">
          <div>
            <label htmlFor="college" className={labelClass}>
              College
            </label>

            <input
              id="college"
              value={form.college}
              onChange={(event) =>
                updateField("college", event.target.value)
              }
              maxLength={200}
              disabled={loading}
              className={inputClass}
            />
          </div>

          <div>
            <label htmlFor="department" className={labelClass}>
              Department
            </label>

            <input
              id="department"
              value={form.department}
              onChange={(event) =>
                updateField("department", event.target.value)
              }
              maxLength={150}
              disabled={loading}
              className={inputClass}
            />
          </div>

          <div>
            <label htmlFor="course" className={labelClass}>
              Course
            </label>

            <input
              id="course"
              value={form.course}
              onChange={(event) =>
                updateField("course", event.target.value)
              }
              maxLength={150}
              disabled={loading}
              className={inputClass}
            />
          </div>

          <div>
            <label htmlFor="yearOfStudy" className={labelClass}>
              Year of Study
            </label>

            <input
              id="yearOfStudy"
              type="number"
              min={1}
              max={10}
              value={form.yearOfStudy}
              onChange={(event) =>
                updateField("yearOfStudy", event.target.value)
              }
              disabled={loading}
              className={inputClass}
            />
          </div>

          <div>
            <label htmlFor="graduationYear" className={labelClass}>
              Graduation Year
            </label>

            <input
              id="graduationYear"
              type="number"
              min={2000}
              max={2100}
              value={form.graduationYear}
              onChange={(event) =>
                updateField(
                  "graduationYear",
                  event.target.value,
                )
              }
              disabled={loading}
              className={inputClass}
            />
          </div>

          <div>
            <label htmlFor="location" className={labelClass}>
              Location
            </label>

            <input
              id="location"
              value={form.location}
              onChange={(event) =>
                updateField("location", event.target.value)
              }
              maxLength={200}
              disabled={loading}
              placeholder="e.g. Hyderabad, India"
              className={inputClass}
            />
          </div>
        </div>
      </section>

      <section className="border-t border-slate-100 pt-7">
        <h2 className="text-lg font-bold text-slate-900">
          Online Presence
        </h2>

        <div className="mt-5 space-y-5">
          <div>
            <label
              htmlFor="profileImageUrl"
              className={labelClass}
            >
              Profile Image URL
            </label>

            <input
              id="profileImageUrl"
              type="url"
              value={form.profileImageUrl}
              onChange={(event) =>
                updateField(
                  "profileImageUrl",
                  event.target.value,
                )
              }
              maxLength={1000}
              disabled={loading}
              placeholder="https://..."
              className={inputClass}
            />
          </div>

          <div className="grid gap-5 sm:grid-cols-2">
            <div>
              <label htmlFor="githubUrl" className={labelClass}>
                GitHub URL
              </label>

              <input
                id="githubUrl"
                type="url"
                value={form.githubUrl}
                onChange={(event) =>
                  updateField("githubUrl", event.target.value)
                }
                maxLength={500}
                disabled={loading}
                placeholder="https://github.com/..."
                className={inputClass}
              />
            </div>

            <div>
              <label
                htmlFor="linkedinUrl"
                className={labelClass}
              >
                LinkedIn URL
              </label>

              <input
                id="linkedinUrl"
                type="url"
                value={form.linkedinUrl}
                onChange={(event) =>
                  updateField(
                    "linkedinUrl",
                    event.target.value,
                  )
                }
                maxLength={500}
                disabled={loading}
                placeholder="https://linkedin.com/in/..."
                className={inputClass}
              />
            </div>
          </div>

          <div>
            <label
              htmlFor="portfolioUrl"
              className={labelClass}
            >
              Portfolio URL
            </label>

            <input
              id="portfolioUrl"
              type="url"
              value={form.portfolioUrl}
              onChange={(event) =>
                updateField(
                  "portfolioUrl",
                  event.target.value,
                )
              }
              maxLength={500}
              disabled={loading}
              placeholder="https://..."
              className={inputClass}
            />
          </div>
        </div>
      </section>

      <section className="border-t border-slate-100 pt-7">
        <h2 className="text-lg font-bold text-slate-900">
          Availability
        </h2>

        <textarea
          id="availability"
          value={form.availability}
          onChange={(event) =>
            updateField(
              "availability",
              event.target.value,
            )
          }
          maxLength={500}
          rows={3}
          disabled={loading}
          placeholder="e.g. Available on weekends for skill exchanges"
          className={`${inputClass} resize-y`}
        />
      </section>

      <div className="flex flex-col-reverse gap-3 border-t border-slate-100 pt-6 sm:flex-row sm:justify-end">
        <button
          type="button"
          onClick={onCancel}
          disabled={loading}
          className="rounded-xl border border-slate-200 px-5 py-2.5 text-sm font-semibold text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
        >
          Cancel
        </button>

        <button
          type="submit"
          disabled={loading}
          className="rounded-xl bg-indigo-600 px-6 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-60"
        >
          {loading ? "Saving..." : "Save Profile"}
        </button>
      </div>
    </form>
  );
};

export default ProfileEditForm;
