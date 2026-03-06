import type { PaginationMeta } from '../api/types';

type Props = {
  meta: PaginationMeta;
  onPageChange: (nextPage: number) => void;
};

export function PaginationBar({ meta, onPageChange }: Props) {
  return (
    <div className="pagination-bar" aria-label="Pagination controls">
      <button
        className="btn btn--ghost"
        onClick={() => onPageChange(meta.page - 1)}
        disabled={!meta.hasPrevious}
      >
        Anterior
      </button>
      <span>
        Página {meta.page + 1} de {Math.max(meta.totalPages, 1)}
      </span>
      <button
        className="btn btn--ghost"
        onClick={() => onPageChange(meta.page + 1)}
        disabled={!meta.hasNext}
      >
        Siguiente
      </button>
    </div>
  );
}
