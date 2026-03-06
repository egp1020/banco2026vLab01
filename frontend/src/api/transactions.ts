import { http } from './http';
import type { CreateTransferInput, PagedResponse, Transaction } from './types';

export async function createTransfer(input: CreateTransferInput): Promise<Transaction> {
  return http<Transaction>('/transactions', { method: 'POST', body: input });
}

export async function fetchTransactionsByAccount(
  accountNumber: string,
  page: number,
  size: number
): Promise<PagedResponse<Transaction>> {
  const query = new URLSearchParams({ page: String(page), size: String(size) });
  return http<PagedResponse<Transaction>>(`/transactions/account/${accountNumber}?${query.toString()}`);
}
