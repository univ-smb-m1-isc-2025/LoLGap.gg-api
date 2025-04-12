import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { Invitation } from '@/types/invitation.types';
import { api } from '@/services/api';

export const useInvitationStore = defineStore('invitation', () => {
    const invitations = ref<Invitation[]>([]);
    const loading = ref(false);
    const error = ref<string | null>(null);

    const fetchInvitations = async () => {
        loading.value = true;
        error.value = null;
        try {
            const response = await api.get('/api/invitations');
            invitations.value = response.data;
        } catch (err) {
            error.value = 'Failed to fetch invitations';
            console.error(err);
        } finally {
            loading.value = false;
        }
    };

    const respondToInvitation = async (invitationId: number, accept: boolean) => {
        loading.value = true;
        error.value = null;
        try {
            const response = await api.post(`/api/invitations/${invitationId}/respond`, { accept });
            const updatedInvitation = response.data;
            const index = invitations.value.findIndex(inv => inv.id === invitationId);
            if (index !== -1) {
                invitations.value[index] = updatedInvitation;
            }
        } catch (err) {
            error.value = 'Failed to respond to invitation';
            console.error(err);
        } finally {
            loading.value = false;
        }
    };

    return {
        invitations,
        loading,
        error,
        fetchInvitations,
        respondToInvitation
    };
}); 