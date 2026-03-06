export type ApiError = {
  timestamp: string;
  status: number;
  error: string;
  code: string;
  message: string;
  path: string;
};

export type PaginationMeta = {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  numberOfElements: number;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
};

export type PagedResponse<T> = {
  data: T[];
  meta: PaginationMeta;
};

export type Customer = {
  id: number;
  accountNumber: string;
  firstName: string;
  lastName: string;
  balance: number | string;
};

export type Transaction = {
  id: number;
  senderAccountNumber: string;
  receiverAccountNumber: string;
  amount: number | string;
  timestamp: string;
  idempotencyKey?: string;
};

export type CreateTransferInput = {
  senderAccountNumber: string;
  receiverAccountNumber: string;
  amount: number;
  idempotencyKey: string;
};
