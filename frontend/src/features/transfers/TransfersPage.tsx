import { z } from 'zod';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation } from '@tanstack/react-query';
import { createTransfer } from '../../api/transactions';
import { ErrorNotice } from '../../components/ErrorNotice';
import { formatCurrency } from '../../lib/format';

const schema = z.object({
  senderAccountNumber: z.string().min(6, 'Mínimo 6 dígitos').max(20, 'Máximo 20 dígitos'),
  receiverAccountNumber: z.string().min(6, 'Mínimo 6 dígitos').max(20, 'Máximo 20 dígitos'),
  amount: z.coerce.number().positive('El monto debe ser mayor a 0'),
});

type TransferForm = z.infer<typeof schema>;

function makeIdempotencyKey() {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
    return crypto.randomUUID();
  }
  return `tx-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`;
}

export function TransfersPage() {
  const form = useForm<TransferForm>({
    resolver: zodResolver(schema),
    defaultValues: {
      senderAccountNumber: '',
      receiverAccountNumber: '',
      amount: 0,
    },
  });

  const mutation = useMutation({
    mutationFn: (values: TransferForm) =>
      createTransfer({
        ...values,
        idempotencyKey: makeIdempotencyKey(),
      }),
  });

  const onSubmit = form.handleSubmit((values) => mutation.mutate(values));

  return (
    <section className="panel">
      <header className="panel__header">
        <h2>Realizar Transferencia</h2>
        <p>Operación transaccional con validación local y control de idempotencia.</p>
      </header>

      <form className="form-grid" onSubmit={onSubmit}>
        <label>
          Cuenta remitente
          <input {...form.register('senderAccountNumber')} inputMode="numeric" placeholder="123456" />
          {form.formState.errors.senderAccountNumber && (
            <small>{form.formState.errors.senderAccountNumber.message}</small>
          )}
        </label>

        <label>
          Cuenta receptora
          <input {...form.register('receiverAccountNumber')} inputMode="numeric" placeholder="654321" />
          {form.formState.errors.receiverAccountNumber && (
            <small>{form.formState.errors.receiverAccountNumber.message}</small>
          )}
        </label>

        <label>
          Monto
          <input {...form.register('amount')} type="number" step="0.01" min="0.01" placeholder="50000" />
          {form.formState.errors.amount && <small>{form.formState.errors.amount.message}</small>}
        </label>

        <button className="btn" type="submit" disabled={mutation.isPending}>
          {mutation.isPending ? 'Enviando...' : 'Transferir'}
        </button>
      </form>

      {mutation.error && <ErrorNotice error={mutation.error} />}

      {mutation.data && (
        <article className="success-card">
          <h3>Transferencia registrada</h3>
          <p><strong>ID:</strong> {mutation.data.id}</p>
          <p><strong>Monto:</strong> {formatCurrency(mutation.data.amount)}</p>
          <p><strong>Origen:</strong> {mutation.data.senderAccountNumber}</p>
          <p><strong>Destino:</strong> {mutation.data.receiverAccountNumber}</p>
        </article>
      )}
    </section>
  );
}
