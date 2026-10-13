import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";

import studentService from "../../services/student/studentService";
import {
  getStudentName,
  type StudentSummary,
} from "../../types/student/student.types";

const StudentDiscoveryPage = () => {
  const navigate = useNavigate();

  const [students, setStudents] = useState<StudentSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");

  useEffect(() => {
    let mounted = true;

    const loadStudents = async () => {
      try {
        setLoading(true);
        setError("");

        const response = await studentService.getStudents();

        if (mounted) {
          setStudents(response);
        }
      } catch (err) {
        console.error("Failed to load students:", err);

        if (mounted) {
          setError(
            "Unable to load students. Please try again."
          );
        }
      } finally {
        if (mounted) {
          setLoading(false);
        }
      }
    };

    void loadStudents();

    return () => {
      mounted = false;
    };
  }, []);

  const filteredStudents = useMemo(() => {
    const query = search.trim().toLowerCase();

    if (!query) {
      return students;
    }

    return students.filter((student) => {
      const searchableText = [
        student.firstName,
        student.lastName,
        student.headline,
        student.college,
        student.department,
        student.course,
        student.location,
      ]
        .filter(Boolean)
        .join(" ")
        .toLowerCase();

      return searchableText.includes(query);
    });
  }, [students, search]);

  const openProfile = (userId: number) => {
    navigate(`/students/${userId}`);
  };

  if (loading) {
    return (
      <div className="min-h-[70vh] flex items-center justify-center">
        <div className="text-center">
          <div className="mx-auto mb-4 h-10 w-10 animate-spin rounded-full border-4 border-slate-200 border-t-slate-900" />
          <p className="text-sm text-slate-500">
            Finding students...
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-50 px-4 py-8 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-7xl">
        <div className="mb-8">
          <div className="mb-2 text-sm font-semibold text-indigo-600">
            Student Community
          </div>

          <h1 className="text-3xl font-black tracking-tight text-slate-900 sm:text-4xl">
            Discover Students
          </h1>

          <p className="mt-2 max-w-2xl text-slate-600">
            Discover students, explore their profiles and connect
            through Message or Video Call.
          </p>
        </div>

        <div className="mb-8 rounded-2xl border border-slate-200 bg-white p-4 shadow-sm">
          <div className="relative">
            <input
              type="text"
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              placeholder="Search by name, college, department, course..."
              className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 pl-11 text-sm outline-none transition focus:border-indigo-500 focus:bg-white focus:ring-2 focus:ring-indigo-100"
            />

            <span className="pointer-events-none absolute left-4 top-1/2 -translate-y-1/2 text-slate-400">
              🔎
            </span>
          </div>
        </div>

        {error && (
          <div className="mb-6 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {error}
          </div>
        )}

        {!error && filteredStudents.length === 0 && (
          <div className="rounded-2xl border border-slate-200 bg-white px-6 py-16 text-center shadow-sm">
            <div className="mb-3 text-4xl">👥</div>

            <h2 className="text-lg font-bold text-slate-900">
              No students found
            </h2>

            <p className="mt-1 text-sm text-slate-500">
              Try another search.
            </p>
          </div>
        )}

        {filteredStudents.length > 0 && (
          <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
            {filteredStudents.map((student) => {
              const name = getStudentName(student);

              return (
                <button
                  key={student.userId}
                  type="button"
                  onClick={() => openProfile(student.userId)}
                  className="group overflow-hidden rounded-2xl border border-slate-200 bg-white text-left shadow-sm transition hover:-translate-y-1 hover:border-indigo-200 hover:shadow-xl"
                >
                  <div className="h-24 bg-gradient-to-r from-indigo-600 via-violet-600 to-purple-600" />

                  <div className="relative px-5 pb-5">
                    <div className="-mt-10 mb-4">
                      {student.profileImageUrl ? (
                        <img
                          src={student.profileImageUrl}
                          alt={name}
                          className="h-20 w-20 rounded-2xl border-4 border-white bg-white object-cover shadow-md"
                        />
                      ) : (
                        <div className="flex h-20 w-20 items-center justify-center rounded-2xl border-4 border-white bg-slate-100 text-2xl font-black text-slate-500 shadow-md">
                          {name.charAt(0).toUpperCase()}
                        </div>
                      )}
                    </div>

                    <div className="flex items-start justify-between gap-3">
                      <div className="min-w-0">
                        <h2 className="truncate text-lg font-bold text-slate-900">
                          {name}
                        </h2>

                        {student.headline && (
                          <p className="mt-1 line-clamp-2 text-sm text-slate-600">
                            {student.headline}
                          </p>
                        )}
                      </div>

                      {student.profileCompleted && (
                        <span
                          title="Profile completed"
                          className="shrink-0 rounded-full bg-emerald-50 px-2 py-1 text-xs font-semibold text-emerald-700"
                        >
                          ✓
                        </span>
                      )}
                    </div>

                    <div className="mt-5 space-y-2 text-sm">
                      {student.college && (
                        <div className="flex gap-2 text-slate-600">
                          <span>🎓</span>
                          <span className="truncate">
                            {student.college}
                          </span>
                        </div>
                      )}

                      {student.department && (
                        <div className="flex gap-2 text-slate-600">
                          <span>📚</span>
                          <span className="truncate">
                            {student.department}
                          </span>
                        </div>
                      )}

                      {student.location && (
                        <div className="flex gap-2 text-slate-600">
                          <span>📍</span>
                          <span className="truncate">
                            {student.location}
                          </span>
                        </div>
                      )}
                    </div>

                    <div className="mt-5 flex items-center justify-between border-t border-slate-100 pt-4">
                      <span className="text-sm font-semibold text-indigo-600">
                        View Profile
                      </span>

                      <span className="text-slate-400 transition group-hover:translate-x-1 group-hover:text-indigo-600">
                        →
                      </span>
                    </div>
                  </div>
                </button>
              );
            })}
          </div>
        )}

        {filteredStudents.length > 0 && (
          <div className="mt-6 text-center text-sm text-slate-500">
            Showing {filteredStudents.length} student
            {filteredStudents.length === 1 ? "" : "s"}
          </div>
        )}
      </div>
    </div>
  );
};

export default StudentDiscoveryPage;