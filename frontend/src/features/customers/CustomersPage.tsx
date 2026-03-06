import { useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { deleteCustomer, fetchCustomers, type UpdateCustomerInput, updateCustomer } from '../../api/customers';
import type { Customer } from '../../api/types';
import { ErrorNotice } from '../../components/ErrorNotice';
import { PaginationBar } from '../../components/PaginationBar';
import { formatCurrency } from '../../lib/format';

const PAGE_SIZE = 20;

const schema = z.object({
  accountNumber: z.string().regex(/^[0-9]{6,20}$/, 'La cuenta debe tener entre 6 y 20 dígitos'),
  firstName: z.string().min(1, 'Nombre requerido').max(50, 'Máximo 50 caracteres'),
  lastName: z.string().min(1, 'Apellido requerido').max(50, 'Máximo 50 caracteres'),
  balance: z.coerce.number().min(0, 'El saldo no puede ser negativo'),
});

type CustomerForm = z.infer<typeof schema>;

export function CustomersPage() {
  const [page, setPage] = useState(0);
  const [editingCustomer, setEditingCustomer] = useState<Customer | null>(null);
  const queryClient = useQueryClient();

  const query = useQuery({
    queryKey: ['customers', page],
    queryFn: () => fetchCustomers(page, PAGE_SIZE),
    staleTime: 15_000,
  });

  const customers = useMemo(() => query.data?.data ?? [], [query.data]);

  const form = useForm<CustomerForm>({
    resolver: zodResolver(schema),
    defaultValues: {
      accountNumber: '',
      firstName: '',
      lastName: '',
      balance: 0,
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: UpdateCustomerInput }) => updateCustomer(id, payload),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['customers'] });
      setEditingCustomer(null);
      form.reset();
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => deleteCustomer(id),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['customers'] });
    },
  });

  const startEditing = (customer: Customer) => {
    setEditingCustomer(customer);
    form.reset({
      accountNumber: customer.accountNumber,
      firstName: customer.firstName,
      lastName: customer.lastName,
      balance: Number(customer.balance),
    });
  };

  const submitEdit = form.handleSubmit((values) => {
    if (!editingCustomer) {
      return;
    }
    updateMutation.mutate({ id: editingCustomer.id, payload: values });
  });

  const handleDelete = (customer: Customer) => {
    const confirmDelete = window.confirm(
      `¿Seguro que quieres eliminar al cliente ${customer.firstName} ${customer.lastName}?`
    );
    if (confirmDelete) {
      deleteMutation.mutate(customer.id);
    }
  };

  return (
    <section className="panel">
      <header className="panel__header">
        <h2>Consultar Clientes</h2>
        <p>Consulta, edición y eliminación de datos de clientes.</p>
      </header>

      {query.isLoading && <p className="skeleton">Cargando clientes...</p>}
      {query.error && <ErrorNotice error={query.error} />}
      {updateMutation.error && <ErrorNotice error={updateMutation.error} />}
      {deleteMutation.error && <ErrorNotice error={deleteMutation.error} />}

      {!query.isLoading && !query.error && (
        <>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Cuenta</th>
                  <th>Nombre</th>
                  <th>Saldo</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {customers.map((customer) => (
                  <tr key={customer.id}>
                    <td>{customer.id}</td>
                    <td>{customer.accountNumber}</td>
                    <td>
                      {customer.firstName} {customer.lastName}
                    </td>
                    <td>{formatCurrency(customer.balance)}</td>
                    <td>
                      <div className="row-actions">
                        <Link
                          className="btn btn--tiny"
                          to={`/historial?account=${encodeURIComponent(customer.accountNumber)}`}
                        >
                          Histórico
                        </Link>
                        <button className="btn btn--tiny btn--edit" onClick={() => startEditing(customer)}>
                          Editar
                        </button>
                        <button
                          className="btn btn--tiny btn--danger"
                          onClick={() => handleDelete(customer)}
                          disabled={deleteMutation.isPending}
                        >
                          Eliminar
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {query.data && <PaginationBar meta={query.data.meta} onPageChange={setPage} />}
        </>
      )}

      {editingCustomer && (
        <div className="modal-backdrop" role="dialog" aria-modal="true" aria-label="Editar cliente">
          <div className="modal-card">
            <h3>Editar cliente #{editingCustomer.id}</h3>
            <form className="form-grid" onSubmit={submitEdit}>
              <label>
                Cuenta
                <input {...form.register('accountNumber')} inputMode="numeric" />
                {form.formState.errors.accountNumber && <small>{form.formState.errors.accountNumber.message}</small>}
              </label>

              <label>
                Nombre
                <input {...form.register('firstName')} />
                {form.formState.errors.firstName && <small>{form.formState.errors.firstName.message}</small>}
              </label>

              <label>
                Apellido
                <input {...form.register('lastName')} />
                {form.formState.errors.lastName && <small>{form.formState.errors.lastName.message}</small>}
              </label>

              <label>
                Saldo
                <input {...form.register('balance')} type="number" min="0" step="0.01" />
                {form.formState.errors.balance && <small>{form.formState.errors.balance.message}</small>}
              </label>

              <div className="modal-actions">
                <button
                  type="button"
                  className="btn btn--ghost"
                  onClick={() => {
                    setEditingCustomer(null);
                    form.reset();
                  }}
                >
                  Cancelar
                </button>
                <button type="submit" className="btn" disabled={updateMutation.isPending}>
                  {updateMutation.isPending ? 'Guardando...' : 'Guardar cambios'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </section>
  );
}
