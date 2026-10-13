
interface LoadingScreenProps {
  message?: string;
}

const LoadingScreen = ({
  message = "Loading SkillSwap Campus...",
}: LoadingScreenProps) => {
  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-50 px-6">
      <div className="flex flex-col items-center justify-center text-center">
        <div
          className="h-10 w-10 animate-spin rounded-full border-4 border-slate-200 border-t-indigo-600"
          aria-hidden="true"
        />

        <p className="mt-4 text-sm font-medium text-slate-600">
          {message}
        </p>
      </div>
    </div>
  );
};

export default LoadingScreen;

