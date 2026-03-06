import { http } from './http';
import type { Customer, PagedResponse } from './types';

export async function fetchCustomers(page: number, size: number): Promise<PagedResponse<Customer>> {
  const query = new URLSearchParams({ page: String(page), size: String(size) });
  return http<PagedResponse<Customer>>(`/customers?${query.toString()}`);
}

export async function fetchCustomerById(id: number): Promise<Customer> {
  return http<Customer>(`/customers/${id}`);
}

export type UpdateCustomerInput = {
  accountNumber: string;
  firstName: string;
  lastName: string;
  balance: number;
};

export async function updateCustomer(id: number, input: UpdateCustomerInput): Promise<Customer> {
  return http<Customer>(`/customers/${id}`, { method: 'PUT', body: input });
}

export async function deleteCustomer(id: number): Promise<void> {
  return http<void>(`/customers/${id}`, { method: 'DELETE' });
}
